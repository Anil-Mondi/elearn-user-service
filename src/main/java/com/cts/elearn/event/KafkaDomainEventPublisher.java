package com.cts.elearn.event;

import com.cts.elearn.domain.event.DomainEvent;
import com.cts.elearn.domain.event.PasswordResetRequestedEvent;
import com.cts.elearn.domain.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "event.publisher",
        havingValue = "kafka"
)
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private static final String CORRELATION_ID = "X-Correlation-ID";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DomainEvent event) {

        String correlationId = MDC.get("correlationId");

        if (event instanceof UserRegisteredEvent registeredEvent) {

            publishEvent(
                    "user.registered",
                    registeredEvent.getEmail(),
                    registeredEvent,
                    correlationId
            );

        } else if (event instanceof PasswordResetRequestedEvent resetEvent) {

            publishEvent(
                    "user.password.reset",
                    resetEvent.getEmail(),
                    resetEvent,
                    correlationId
            );

        } else {

            log.warn("Unsupported event type: {}", event.getClass().getSimpleName());

        }
    }

    private void publishEvent(String topic,
                              String key,
                              Object payload,
                              String correlationId) {

        Message<Object> message = MessageBuilder
                .withPayload(payload)
                .setHeader("kafka_messageKey", key)
                .setHeader(CORRELATION_ID, correlationId)
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {

                    if (ex != null) {

                        log.error(
                                "Kafka publish failed | topic={} | correlationId={}",
                                topic,
                                correlationId,
                                ex
                        );

                    } else {

                        log.info(
                                "Kafka publish success | topic={} | partition={} | offset={} | correlationId={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                correlationId
                        );

                    }

                });
    }
}