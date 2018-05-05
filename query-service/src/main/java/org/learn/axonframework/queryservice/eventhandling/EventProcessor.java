package org.learn.axonframework.queryservice.eventhandling;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.InvoiceCompensatedEvent;
import org.learn.axonframework.coreapi.InvoicePreparationFailedEvent;
import org.learn.axonframework.coreapi.InvoicePreparedEvent;
import org.learn.axonframework.coreapi.OrderCompletedEvent;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.coreapi.ShipmentCompensatedEvent;
import org.learn.axonframework.coreapi.ShipmentPreparationFailedEvent;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.queryservice.model.Invoice;
import org.learn.axonframework.queryservice.model.Order;
import org.learn.axonframework.queryservice.model.Shipment;
import org.learn.axonframework.queryservice.repository.InvoiceRepository;
import org.learn.axonframework.queryservice.repository.OrderRepository;
import org.learn.axonframework.queryservice.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("amqpEvents")
@Component
public class EventProcessor {

    public static final String NOT_AVAILABLE = "N/A";

    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @EventHandler
    public void on(ShipmentPreparedEvent event) {
        log.info("on ShipmentPreparedEvent");
        shipmentRepository.save(new Shipment(event.getShipmentId(), event.getOrderId(), event.getPrice()));
    }

    @EventHandler
    public void on(ShipmentPreparationFailedEvent event) {
        log.info("on ShipmentPreparationFailedEvent");
        shipmentRepository.save(new Shipment(event.getShipmentId(), event.getOrderId(), -1));
    }

    @EventHandler
    public void on(ShipmentCompensatedEvent event) {
        log.info("on ShipmentCompensatedEvent");
        Shipment shipment = shipmentRepository.findOne(event.getShipmentId());
        shipment.setPrice(-1);
        shipmentRepository.save(shipment);
    }

    @EventHandler
    public void on(InvoicePreparedEvent event) {
        log.info("on InvoicePreparedEvent");
        invoiceRepository.save(new Invoice(event.getInvoiceId(), event.getOrderId(), event.getInvoice()));
    }

    @EventHandler
    public void on(InvoicePreparationFailedEvent event) {
        log.info("on InvoicePreparationFailedEvent");
        invoiceRepository.save(new Invoice(event.getInvoiceId(), event.getOrderId(), NOT_AVAILABLE));
    }

    @EventHandler
    public void on(InvoiceCompensatedEvent event) {
        log.info("on InvoiceCompensatedEvent");
        Invoice invoice = invoiceRepository.findOne(event.getInvoiceId());
        invoice.setInvoiceString(NOT_AVAILABLE);
        invoiceRepository.save(invoice);
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        log.info("on OrderCompletedEvent");
        ProductInfo productInfo = event.getProductInfo();
        orderRepository.save(new Order(event.getOrderId(), productInfo.getProductId(),
                productInfo.getComment(), productInfo.getPrice()));
    }


}
