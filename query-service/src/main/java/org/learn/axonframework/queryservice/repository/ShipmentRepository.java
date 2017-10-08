package org.learn.axonframework.queryservice.repository;

import org.learn.axonframework.queryservice.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, String> {
}
