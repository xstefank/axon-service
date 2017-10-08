package org.learn.axonframework.queryservice.rest;

import org.learn.axonframework.queryservice.model.Shipment;
import org.learn.axonframework.queryservice.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipmentController {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @GetMapping("/shipments")
    public List<Shipment> getShipments() {
        return shipmentRepository.findAll();
    }

}
