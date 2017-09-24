package org.learn.axonframework.shipmentservice.model;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@NoArgsConstructor
@Entity
@Aggregate
public class Shipment {

    @Id
    private String id;

    @AggregateIdentifier
    private String orderId;

    private String productName;

    private int price;

    public Shipment(String id, String orderId, String productName, int price) {
        this.id = id;
        this.orderId = orderId;
        this.productName = productName;
        this.price = price;
    }

    @CommandHandler
    public Shipment(PrepareShipmentCommand command) {
        LoggerFactory.getLogger("TEST").info("received test command - " + command.getOrderId());
        apply(new ShipmentPreparedEvent("mySuperID", command.getOrderId(), 100));
    }

    @EventSourcingHandler
    public void on(ShipmentPreparedEvent event) {
        this.id = event.getShipmentId();
        this.orderId = event.getOrderId();
        this.price = event.getPrice();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
