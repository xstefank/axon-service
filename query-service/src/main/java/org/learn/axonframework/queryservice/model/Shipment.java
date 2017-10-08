package org.learn.axonframework.queryservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Shipment {

    @Id
    private String id;

    private String orderId;

    private int price;

    public Shipment() {
    }

    public Shipment(String id, String orderId, int price) {
        this.id = id;
        this.orderId = orderId;
        this.price = price;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
