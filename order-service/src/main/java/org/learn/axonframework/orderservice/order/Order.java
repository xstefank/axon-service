package org.learn.axonframework.orderservice.order;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.learn.axonframework.coreapi.FileOrderCommand;
import org.learn.axonframework.coreapi.OrderFiledEvent;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@NoArgsConstructor
@Aggregate
public class Order {

    @AggregateIdentifier
    private String orderId;

    @CommandHandler
    public Order(FileOrderCommand command) {
        apply(new OrderFiledEvent(command.getOrderId(), command.getProductInfo()));
    }

    @EventSourcingHandler
    public void on(OrderFiledEvent event) {
        orderId = event.getOrderId();
    }

}
