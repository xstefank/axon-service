package org.learn.axonframework.invoiceservice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.learn.axonframework.coreapi.CompensateInvoiceCommand;
import org.learn.axonframework.coreapi.InvoiceCompensatedEvent;
import org.learn.axonframework.coreapi.InvoicePreparedEvent;
import org.learn.axonframework.coreapi.PrepareInvoiceCommand;
import org.learn.axonframework.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

@NoArgsConstructor
@Aggregate
public class Invoice {

    private static final Logger log = LoggerFactory.getLogger(Invoice.class);

    private String id;

    @AggregateIdentifier
    private String orderId;

    @CommandHandler
    public Invoice(PrepareInvoiceCommand command) {
        log.info("received PrepareInvoiceCommand command for order: " + command.getOrderId());
        String id = Util.generateId();

        //generate invoice
        String invoice = generateInvoice();

        apply(new InvoicePreparedEvent(id, command.getOrderId(), invoice));
    }

    private String generateInvoice() {
        return "This is just the invoice stub";
    }

    @CommandHandler
    public void handle(CompensateInvoiceCommand command) {
        log.info("received CompensateInvoiceCommand command");
        markDeleted();
        apply(new InvoiceCompensatedEvent(id, orderId, command.getCause()));
    }

    @EventSourcingHandler
    public void on(InvoicePreparedEvent event) {
        this.id = event.getInvoiceId();
        this.orderId = event.getOrderId();
    }

    public Invoice(String id, String orderId) {
        this.id = id;
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }
}
