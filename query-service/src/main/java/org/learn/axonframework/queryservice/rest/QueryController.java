package org.learn.axonframework.queryservice.rest;

import org.learn.axonframework.queryservice.model.Invoice;
import org.learn.axonframework.queryservice.model.Order;
import org.learn.axonframework.queryservice.model.Shipment;
import org.learn.axonframework.queryservice.repository.InvoiceRepository;
import org.learn.axonframework.queryservice.repository.OrderRepository;
import org.learn.axonframework.queryservice.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api")
public class QueryController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderRepository.findOne(orderId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/shipments")
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<Shipment> getShipment(@PathVariable String shipmentId) {
        Shipment shipment = shipmentRepository.findOne(shipmentId);
        return shipment != null ? ResponseEntity.ok(shipment) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/invoices")
    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<Invoice> getInvoice(String invoiceId) {
        Invoice invoice = invoiceRepository.findOne(invoiceId);
        return invoice != null ? ResponseEntity.ok(invoice) : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
