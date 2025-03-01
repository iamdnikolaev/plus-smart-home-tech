package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioAddedEventHandler extends HubProtoHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    public HubEventAvro mapToAvro(HubEventProto eventProto) {
        ScenarioAddedEventProto scenarioAddedEventProto = eventProto.getScenarioAdded();
        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEventProto.getName())
                .build();

        return HubEventAvro.newBuilder()
                .setHubId(eventProto.getHubId())
                .setTimestamp(Instant.ofEpochSecond(eventProto.getTimestamp().getSeconds(),
                        eventProto.getTimestamp().getNanos()))
                .setPayload(scenarioAddedEventAvro)
                .build();
    }
}
