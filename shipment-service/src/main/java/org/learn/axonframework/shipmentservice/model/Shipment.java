package org.learn.axonframework.shipmentservice.model;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@NoArgsConstructor
@Entity
@Aggregate
public class Shipment {

    private static final Logger log = LoggerFactory.getLogger(Shipment.class);

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
        log.info("received PrepareShipmentCommand command for order: " + command.getOrderId());
        String id = Util.generateId();

        //compute shipment
        int shipment = 100;

        apply(new ShipmentPreparedEvent(id, command.getOrderId(), shipment));
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
