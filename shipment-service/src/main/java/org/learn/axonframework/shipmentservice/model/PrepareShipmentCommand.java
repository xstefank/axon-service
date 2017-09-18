package org.learn.axonframework.shipmentservice.model;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class PrepareShipmentCommand {

    @TargetAggregateIdentifier
    private final String id;
    private final String orderId;
    private final int price;

    public PrepareShipmentCommand(String id, String orderId, int price) {
        this.id = id;
        this.orderId = orderId;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getPrice() {
        return price;
    }
}
