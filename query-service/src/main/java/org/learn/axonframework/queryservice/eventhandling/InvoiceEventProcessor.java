package org.learn.axonframework.queryservice.eventhandling;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.learn.axonframework.coreapi.InvoicePreparedEvent;
import org.learn.axonframework.queryservice.model.Invoice;
import org.learn.axonframework.queryservice.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ProcessingGroup("invoiceEvents")
@Component
public class InvoiceEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(InvoiceEventProcessor.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @EventHandler
    public void on(InvoicePreparedEvent event) {
        log.info("on InvoicePreparedEvent");
        invoiceRepository.save(new Invoice(event.getInvoiceId(), event.getOrderId(), event.getInvoice()));
    }


}
