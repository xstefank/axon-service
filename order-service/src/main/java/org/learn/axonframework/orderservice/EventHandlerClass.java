package org.learn.axonframework.orderservice;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.springframework.stereotype.Component;

@ProcessingGroup("amqpEvents")
@Component
public class EventHandlerClass {

    @EventHandler
    public void on(ShipmentPreparedEvent event) {
        System.out.println("XXXXXXXXXXXXXXXXXX" + event.getOrderId());
    }
}
