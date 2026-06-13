package com.cts.elearn.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.cts.elearn.domain.event.DomainEvent;

@Component
@Primary
public class LocalDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(LocalDomainEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {

        log.info("EVENT RECEIVED LOCALLY : {}", event);

        // Kafka disabled for now
    }
}