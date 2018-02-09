package org.learn.axonframework.orderservice.command;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class CancelOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;

    public CancelOrderCommand(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
