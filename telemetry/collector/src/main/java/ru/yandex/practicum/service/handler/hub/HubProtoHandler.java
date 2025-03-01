package ru.yandex.practicum.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.CollectorService;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class HubProtoHandler implements HubEventHandler {
    @Value("${topic.telemetry.hubs}")
    private String topicTelemetryHubs;

    private static CollectorService service;

    @Override
    public void handle(HubEventProto eventProto) {
        log.info("==> handle hub eventProto = {}", eventProto);
        HubEventAvro eventAvro = mapToAvro(eventProto);

        service.sendEvent(eventAvro, topicTelemetryHubs, eventAvro.getHubId());
    }

    public abstract HubEventAvro mapToAvro(HubEventProto eventProto);
}