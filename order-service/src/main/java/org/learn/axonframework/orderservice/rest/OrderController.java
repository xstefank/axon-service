package org.learn.axonframework.orderservice.rest;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.learn.axonframework.coreapi.FileOrderCommand;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.util.LoggingCallback;
import org.learn.axonframework.util.Util;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OrderController {

    @Autowired
    private CommandGateway commandGateway;

    @PostMapping
    public String createOrder(@RequestBody Map<String, String> request) {
        String orderId = Util.generateId();
        ProductInfo productInfo = new ProductInfo(request.get("productId"),
                request.get("comment"), Integer.valueOf(request.get("price")));

        commandGateway.send(new FileOrderCommand(orderId, productInfo), LoggingCallback.INSTANCE);

        return "Order posted - " + orderId;
    }

}
