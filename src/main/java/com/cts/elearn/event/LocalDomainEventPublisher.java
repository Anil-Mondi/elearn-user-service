package com.cts.elearn.event;

import com.cts.elearn.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "event.publisher",
        havingValue = "local",
        matchIfMissing = true
)
public class LocalDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(DomainEvent event) {

        log.info("Publishing event locally: {}", event);

        // Kafka disabled
    }
}