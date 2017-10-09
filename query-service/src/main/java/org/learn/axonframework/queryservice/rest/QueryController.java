package org.learn.axonframework.queryservice.rest;

import org.learn.axonframework.queryservice.model.Invoice;
import org.learn.axonframework.queryservice.model.Order;
import org.learn.axonframework.queryservice.model.Shipment;
import org.learn.axonframework.queryservice.repository.InvoiceRepository;
import org.learn.axonframework.queryservice.repository.OrderRepository;
import org.learn.axonframework.queryservice.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/shipments")
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }

    @GetMapping("/invoices")
    public List<Invoice> getInvoices() {
        return invoiceRepository.findAll();
    }

    @GetMapping("/orders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }
}
