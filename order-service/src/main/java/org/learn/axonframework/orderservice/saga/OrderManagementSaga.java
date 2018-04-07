package org.learn.axonframework.orderservice.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.EndSaga;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.learn.axonframework.coreapi.CompensateInvoiceCommand;
import org.learn.axonframework.coreapi.CompensateShipmentCommand;
import org.learn.axonframework.coreapi.InvoiceCompensatedEvent;
import org.learn.axonframework.coreapi.InvoicePreparationFailedEvent;
import org.learn.axonframework.coreapi.InvoicePreparedEvent;
import org.learn.axonframework.coreapi.OrderCancelledEvent;
import org.learn.axonframework.coreapi.OrderFiledEvent;
import org.learn.axonframework.coreapi.PrepareInvoiceCommand;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.coreapi.ShipmentCompensatedEvent;
import org.learn.axonframework.coreapi.ShipmentPreparationFailedEvent;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.orderservice.command.OrderCompletedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class OrderManagementSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderManagementSaga.class.getSimpleName());

    private final OrderProcessing orderProcessing = new OrderProcessing();
    private final OrderCompensationProcessing compensationProcessing = new OrderCompensationProcessing();
    private boolean compensating = false;

    private String orderId;
    private ProductInfo productInfo;

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderFiledEvent event) {
        log.info("STARTING SAGA - " + event.getOrderId());

        orderId = event.getOrderId();
        productInfo = event.getProductInfo();

        //request shipment
        commandGateway.send(new PrepareShipmentCommand(orderId, productInfo));
        log.info("PrepareShipmentCommand sent");

        //request invoicecommandGateway.send(new PrepareInvoiceCommand(orderId, productInfo));
        log.info("PrepareInvoiceCommand sent");

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShipmentPreparedEvent event) {
        if (!compensating) {
            log.info("on ShipmentPreparedEvent");
            orderProcessing.setShipmentProcessed(true);

            checkSagaCompleted();
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InvoicePreparedEvent event) {
        if (!compensating) {
            log.info("on InvoicePreparedEvent");
            orderProcessing.setInvoiceProcessed(true);

            checkSagaCompleted();
        }
    }

    private void checkSagaCompleted() {
        if (orderProcessing.isDone()) {
            log.info("saga executed successfully");
            endSaga();
            commandGateway.send(new OrderCompletedCommand(orderId, productInfo));
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCancelledEvent event) {
        log.info("cancelling the saga for order - " + orderId);
        compensateSaga(orderId, "cancelled by user");
    }

    //compensations

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShipmentPreparationFailedEvent event) {
        log.info("on ShipmentPreparationFailedEvent");

        compensateSaga(event.getOrderId(), event.getCause());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InvoicePreparationFailedEvent event) {
        log.info("on InvoicePreparationFailedEvent");

        compensateSaga(event.getOrderId(), event.getCause());
    }

    private void compensateSaga(String orderId, String cause) {
        log.info(String.format("compensation of saga for model [%s] with casuse - %s", orderId, cause));

        compensating = true;
        commandGateway.send(new CompensateShipmentCommand(orderId, cause));
        commandGateway.send(new CompensateInvoiceCommand(orderId, cause));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(ShipmentCompensatedEvent event) {
        log.info("on ShipmentCompensatedEvent");
        compensationProcessing.setShipmentCompensated(true);

        checkSagaCompensated();
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InvoiceCompensatedEvent event) {
        log.info("on InvoiceCompensatedEvent");
        compensationProcessing.setInvoiceCompensated(true);

        checkSagaCompensated();
    }

    private void checkSagaCompensated() {
        if (compensationProcessing.isCompensated()) {
            log.info("saga fully compensated");
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

        public boolean isDone() {
            return shipmentProcessed && invoiceProcessed;
        }

    }

    private static class OrderCompensationProcessing {

        private boolean shipmentCompensated;
        private boolean invoiceCompensated;

        public void setShipmentCompensated(boolean shipmentCompensated) {
            this.shipmentCompensated = shipmentCompensated;
        }

        public void setInvoiceCompensated(boolean invoiceCompensated) {
            this.invoiceCompensated = invoiceCompensated;
        }

        public boolean isCompensated() {
            return shipmentCompensated && invoiceCompensated;
        }
    }

}
