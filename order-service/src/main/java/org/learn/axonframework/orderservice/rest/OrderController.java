package org.learn.axonframework.orderservice.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.orderservice.command.CancelOrderCommand;
import org.learn.axonframework.orderservice.command.FileOrderCommand;
import org.learn.axonframework.util.LoggingCallback;
import org.learn.axonframework.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@SuppressWarnings(value = "unchecked")
public class OrderController {

    private static final Logger log = Logger.getLogger(OrderController.class.getName());

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping("/order")
    public String createOrder(@RequestBody ProductInfo productInfo) {
        String orderId = Util.generateId();

        log.info("before command");
        commandGateway.send(new FileOrderCommand(orderId, productInfo));
        log.info("after command");

        return "OrderAggregate posted - " + orderId;
    }

    @PostMapping("/order/{orderId}/cancel")
    public String cancelOrder(@PathVariable String orderId) {
        commandGateway.send(new CancelOrderCommand(orderId), LoggingCallback.INSTANCE);

        return "Going to cancel order - " + orderId;
    }

}
