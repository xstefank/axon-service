package org.learn.axonframework.queryservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "QUERY_ORDER")
public class Order {

    @Id
    private String id;

    private String productId;
    private String comment;
    private int price;

    public Order() {
    }

    public Order(String id, String productId, String comment, int price) {
        this.id = id;
        this.productId = productId;
        this.comment = comment;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
