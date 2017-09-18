package org.learn.axonframework.shipmentservice;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.ShipmentRequestedEvent;
import org.learn.axonframework.shipmentservice.model.PrepareShipmentCommand;
import org.learn.axonframework.util.LoggingCallback;
import org.learn.axonframework.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("amqpEvents")
@Component
public class ShipmentEventHandler {

    private static final Logger log = LoggerFactory.getLogger(ShipmentEventHandler.class.getSimpleName());

    @Autowired
    private CommandGateway commandGateway;

    @EventHandler
    public void on(ShipmentRequestedEvent event) {
        log.info("on ShipmentRequestedEvent");
        String id = Util.generateId();

        //generate price for shipping

        commandGateway.send(new PrepareShipmentCommand(id, event.getOrderId(), 20), LoggingCallback.INSTANCE);

    }

}
