package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.AvroSerializer;
import ru.yandex.practicum.service.SensorEventDeserializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregationStarter {
    @Value("${kafka.bootstrap-servers}")
    private final String BOOTSTRAP_SERVERS_CONFIG;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    @Value("${topic.telemetry.sensors}")
    private final String SENSORS_TOPIC;
    @Value("${topic.telemetry.snapshots}")
    private final String SNAPSHOTS_TOPIC;
    @Value("${aggregator-client-id}")
    private final String AGGREGATOR_CLIENT_ID;
    @Value("${aggregator-group-id}")
    private final String AGGREGATOR_GROUP_ID;

    private final KafkaConsumer<String, SensorEventAvro> consumer = new KafkaConsumer<>(getConsumerProperties());
    private final KafkaProducer<String, SpecificRecordBase> producer = new KafkaProducer<>(getProducerProperties());
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public void start() {
        try {
            consumer.subscribe(List.of(SENSORS_TOPIC));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    Optional<SensorsSnapshotAvro> sensorsSnapshotAvro = updateState(event);

                    if (sensorsSnapshotAvro.isPresent()) {
                        SensorsSnapshotAvro snapshotAvro = sensorsSnapshotAvro.get();
                        ProducerRecord<String, SpecificRecordBase> producerRecord =
                                new ProducerRecord<>(SNAPSHOTS_TOPIC,
                                        null,
                                        snapshotAvro.getTimestamp().toEpochMilli(),
                                        snapshotAvro.getHubId(),
                                        snapshotAvro);
                        producer.send(producerRecord);
                        log.info("snapshotAvro {} has sent on topic {}", snapshotAvro, SNAPSHOTS_TOPIC);
                    }
                }
                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.warn("Sensor events have got an error", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Consumer is closing");
                consumer.close();
                log.info("Producer is closing");
                producer.close();
            }
        }
    }

    private Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, AGGREGATOR_CLIENT_ID);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, AGGREGATOR_GROUP_ID);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_CONFIG);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        return properties;
    }

    private Properties getProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_CONFIG);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
        return properties;
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("==> Update state for event: {}", event);

        SensorsSnapshotAvro snapshotAvro;
        String hubId = event.getHubId();
        String eventId = event.getId();

        SensorsSnapshotAvro snapshot = snapshots.computeIfAbsent(hubId, s -> {
            SensorsSnapshotAvro newSnapshot = new SensorsSnapshotAvro();
            newSnapshot.setHubId(hubId);
            newSnapshot.setTimestamp(event.getTimestamp());
            newSnapshot.setSensorsState(new HashMap<>());
            return newSnapshot;
        });

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro oldState = sensorsState.get(eventId);
        if (oldState != null && oldState.getTimestamp().isAfter(event.getTimestamp())
                && oldState.getData().equals(event.getPayload())) {
            return Optional.empty();
        }

        SensorStateAvro newState = new SensorStateAvro();
        newState.setTimestamp(event.getTimestamp());
        newState.setData(event.getPayload());
        sensorsState.put(eventId, newState);
        snapshot.setTimestamp(event.getTimestamp());

        log.info("<== Updated snapshot: {}", snapshot);
        return Optional.of(snapshot);
    }
}
