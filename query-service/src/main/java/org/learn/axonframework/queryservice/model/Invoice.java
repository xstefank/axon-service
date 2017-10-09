package org.learn.axonframework.queryservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Invoice {

    @Id
    private String id;

    private String orderId;

    private String invoiceString;

    public Invoice() {
    }

    public Invoice(String id, String orderId, String invoiceString) {
        this.id = id;
        this.orderId = orderId;
        this.invoiceString = invoiceString;
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

    public String getInvoiceString() {
        return invoiceString;
    }

    public void setInvoiceString(String invoiceString) {
        this.invoiceString = invoiceString;
    }
}
