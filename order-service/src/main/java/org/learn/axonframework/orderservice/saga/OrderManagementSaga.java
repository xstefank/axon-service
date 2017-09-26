package org.learn.axonframework.orderservice.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.learn.axonframework.coreapi.InvoicePreparedEvent;
import org.learn.axonframework.coreapi.OrderFiledEvent;
import org.learn.axonframework.coreapi.PrepareInvoiceCommand;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class OrderManagementSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderManagementSaga.class.getSimpleName());

    private final OrderProcessing orderProcessing = new OrderProcessing();

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderFiledEvent event) {
        log.info("STARTING SAGA");

        //request shipment
        log.info("sending PrepareShipmentCommand");
        commandGateway.send(new PrepareShipmentCommand(event.getOrderId(), event.getProductInfo()));

        //request invoice
        log.info("sending PrepareInvoiceCommand");
        commandGateway.send(new PrepareInvoiceCommand(event.getOrderId(), event.getProductInfo()));

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShipmentPreparedEvent event) {
        log.info("on ShipmentPreparedEvent");
        orderProcessing.setShipmentProcessed(true);

        checkSagaCompleted();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InvoicePreparedEvent event) {
        log.info("on InvoicePreparedEvent");
        orderProcessing.setInvoiceProcessed(true);

        checkSagaCompleted();
    }

    private void checkSagaCompleted() {
        if (orderProcessing.isDone()) {
            endSaga();
        }
    }

    @EndSaga
    private void endSaga() {
        log.info("ENDING SAGA");
    }

    private static class OrderProcessing {

        private boolean shipmentProcessed;
        private boolean invoiceProcessed;

        public OrderProcessing() {
        }

        public void setShipmentProcessed(boolean shipmentProcessed) {
            this.shipmentProcessed = shipmentProcessed;
        }

        public void setInvoiceProcessed(boolean invoiceProcessed) {
            this.invoiceProcessed = invoiceProcessed;
        }

        public boolean isInvoiceProcessed() {
            return invoiceProcessed;
        }

        public boolean isShipmentProcessed() {
            return shipmentProcessed;
        }

        public boolean isDone() {
            return shipmentProcessed && invoiceProcessed;
        }
    }

}
