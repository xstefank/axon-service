package org.learn.axonframework.orderservice.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.learn.axonframework.coreapi.OrderFiledEvent;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class OrderManagementSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderManagementSaga.class.getSimpleName());

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderFiledEvent event) {

        //request shipment
        log.info("sending RequestShipmentCommand");
        commandGateway.send(new PrepareShipmentCommand(event.getOrderId(), new ProductInfo()));

        //create invoice
//        commandGateway.send(new CreateInvoiceCommand(event.getOrderId(), event.getProductId(),
//                event.getComment()), LoggingCallback.INSTANCE);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShipmentPreparedEvent event) {
        log.info("on ShipmentPreparedEvent");

    }

}
