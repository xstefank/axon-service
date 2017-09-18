package org.learn.axonframework.shipmentservice;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.messaging.MetaData;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.shipmentservice.model.PrepareShipmentCommand;
import org.learn.axonframework.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@RestController
@Transactional
public class ShipmentEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(ShipmentEventProcessor.class.getSimpleName());

    @Autowired
    private CommandGateway commandGateway;

//    @EventHandler
//    public void on(ShipmentRequestedEvent event) {
//        log.info("on ShipmentRequestedEvent");
//
////        commandGateway.send(new PrepareShipmentCommand("1234", new ProductInfo("1111", "comment", 1)), LoggingCallback.INSTANCE);
////        apply(new ShipmentPreparedEvent("1234", event.getOrderId(), 20));
//    }

    @Autowired
    private EventBus eventBus;

    @PostMapping
    public void whatever() {
        commandGateway.send(new PrepareShipmentCommand(Util.generateId(), "4c0e3f77-8ad8-427e-8ec9-7e6eea545101", 20));
    }




}
