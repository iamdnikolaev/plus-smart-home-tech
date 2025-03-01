package ru.yandex.practicum.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.service.CollectorService;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class SensorProtoHandler implements SensorEventHandler {
    @Value("${topic.telemetry.sensors}")
    private String topicTelemetrySensors;

    private static CollectorService service;

    @Override
    public void handle(SensorEventProto eventProto) {
        log.info("==> handle sensor eventProto = {}", eventProto);
        SensorEventAvro eventAvro = mapToAvro(eventProto);

        service.sendEvent(eventAvro, topicTelemetrySensors, eventAvro.getHubId());
    }

    public abstract SensorEventAvro mapToAvro(SensorEventProto eventProto);
}