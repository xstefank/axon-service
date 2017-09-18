package org.learn.axonframework.shipmentservice;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.interceptors.JSR303ViolationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class TransactionDispatchInterceptor<T extends Message<?>> implements MessageDispatchInterceptor<T> {

    private TransactionManager transactionManager;

    public TransactionDispatchInterceptor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public BiFunction<Integer, T, T> handle(List<T> messages) {
        Transaction transaction = transactionManager.startTransaction();
        return (index, message) -> {
            return message;
        };
    }
}
