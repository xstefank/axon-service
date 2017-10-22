package org.learn.axonframework.orderservice.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.orderservice.command.FileOrderCommand;
import org.learn.axonframework.util.LoggingCallback;
import org.learn.axonframework.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String createOrder(@RequestBody ProductInfo productInfo) {
        String orderId = Util.generateId();

        commandGateway.send(new FileOrderCommand(orderId, productInfo), LoggingCallback.INSTANCE);

        return "OrderAggregate posted - " + orderId;
    }

}
