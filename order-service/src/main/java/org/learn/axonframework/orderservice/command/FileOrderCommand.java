package org.learn.axonframework.orderservice.command;

import org.axonframework.commandhandling.TargetAggregateIdentifier;
import org.learn.axonframework.coreapi.ProductInfo;

public class FileOrderCommand {

    @TargetAggregateIdentifier
    private String orderId;

    private ProductInfo productInfo;

    public FileOrderCommand(String orderId, ProductInfo productInfo) {
        this.orderId = orderId;
        this.productInfo = productInfo;
    }

    public String getOrderId() {
        return orderId;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }
}
