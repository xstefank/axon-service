package org.learn.axonframework.shipmentservice.model;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.learn.axonframework.coreapi.CompensateShipmentCommand;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ProductInfo;
import org.learn.axonframework.coreapi.ShipmentCompensatedEvent;
import org.learn.axonframework.coreapi.ShipmentPreparationFailedEvent;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;
import org.learn.axonframework.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

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
    public Shipment(PrepareShipmentCommand command) throws InterruptedException {
        log.info("received PrepareShipmentCommand command for order: " + command.getOrderId());
        String id = Util.generateId();

        if (command.getProductInfo().getProductId().equals("failShipment")) {
            //simulate saga compensation
            log.info("failing shipment creation");
            apply(new ShipmentPreparationFailedEvent(id, command.getOrderId(), "simulated saga fail"));
        } else {
            if (command.getProductInfo().getProductId().equals("delayShipment")) {
                Thread.sleep(30000);
            }

            //generate invoice
            int shipment = computeShipment(command.getProductInfo());

            apply(new ShipmentPreparedEvent(id, command.getOrderId(), shipment));
        }

    }

    private int computeShipment(ProductInfo productInfo) {
        // testing stub
        return 42;
    }

    @CommandHandler
    public void handle(CompensateShipmentCommand command) {
        log.info("received CompensateShipmentCommand command");

        markDeleted();
        apply(new ShipmentCompensatedEvent(id, orderId, command.getCause()));
    }

    @EventSourcingHandler
    public void on(ShipmentPreparedEvent event) {
        this.id = event.getShipmentId();
        this.orderId = event.getOrderId();
        this.price = event.getPrice();
    }

    @EventSourcingHandler
    public void on(ShipmentPreparationFailedEvent event) {
        this.id = event.getShipmentId();
        this.orderId = event.getOrderId();
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
