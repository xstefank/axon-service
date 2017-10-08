package org.learn.axonframework.queryservice.eventhandling;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.queryservice.model.Shipment;
import org.learn.axonframework.queryservice.repository.ShipmentRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("amqpEvents")
@Component
public class EventProcessor {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @EventHandler
    public void on(ShipmentPreparedEvent event) {
        LoggerFactory.getLogger(EventProcessor.class).info("on ShipmentPreparedEvent");
        shipmentRepository.save(new Shipment(event.getShipmentId(), event.getOrderId(), event.getPrice()));
    }
}
