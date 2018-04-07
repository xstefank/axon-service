package org.learn.axonframework.queryservice.eventhandling;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.queryservice.model.Shipment;
import org.learn.axonframework.queryservice.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("shipmentEvents")
@Component
public class ShipmentEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(ShipmentEventProcessor.class);

    @Autowired
    private ShipmentRepository shipmentRepository;

    @EventHandler
    public void on(ShipmentPreparedEvent event) {
        log.info("on ShipmentPreparedEvent");
        shipmentRepository.save(new Shipment(event.getShipmentId(), event.getOrderId(), event.getPrice()));
    }

}
