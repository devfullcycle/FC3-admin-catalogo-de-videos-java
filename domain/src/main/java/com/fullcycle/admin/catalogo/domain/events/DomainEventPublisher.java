package com.fullcycle.admin.catalogo.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {
    <T extends DomainEvent> void publishEvent(T event);
}
