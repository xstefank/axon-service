package org.learn.axonframework.shipmentservice;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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
        System.out.println("sending testing command");
//        commandGateway.send(new RequestShipmentCommand("7d9162cb-9782-4a00-b67a-470f282cc0d5", new ProductInfo()));
    }




}
