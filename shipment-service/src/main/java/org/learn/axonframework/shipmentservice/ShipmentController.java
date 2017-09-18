package org.learn.axonframework.shipmentservice;

import org.learn.axonframework.shipmentservice.model.Shipment;
import org.learn.axonframework.shipmentservice.model.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipmentController {

    @Autowired
    private ShipmentRepository repository;

    @GetMapping
    public List<Shipment> findAll() {
        return repository.findAll();
    }
}
