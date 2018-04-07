package org.learn.axonframework.queryservice.eventhandling;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.OrderCompletedEvent;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.queryservice.model.Order;
import org.learn.axonframework.queryservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("orderEvents")
@Component
public class OrderEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProcessor.class);

    @Autowired
    private OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCompletedEvent event) {
        log.info("on OrderCompletedEvent");
        ProductInfo productInfo = event.getProductInfo();
        orderRepository.save(new Order(event.getOrderId(), productInfo.getProductId(),
                productInfo.getComment(), productInfo.getPrice()));
    }


}
