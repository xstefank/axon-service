package org.learn.axonframework.orderservice.saga;

import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import org.learn.axonframework.coreapi.OrderFiledEvent;
import org.learn.axonframework.coreapi.PrepareShipmentCommand;
import org.learn.axonframework.coreapi.ProductInfo;

public class OrderManagementSagaTest {

    private static final String ORDER1_ID = "1234";
    private static final String ORDER1_PRODUCT_ID = "testProduct";
    private static final String ORDER1_COMMENT = "testComment";
    private static final int ORDER1_PRICE = 100;

    private SagaTestFixture<OrderManagementSaga> fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new SagaTestFixture<>(OrderManagementSaga.class);
    }

    @Test
    public void testSagaCreated() {
        fixture.givenNoPriorActivity()
                .whenPublishingA(new OrderFiledEvent(ORDER1_ID, new ProductInfo(ORDER1_PRODUCT_ID, ORDER1_COMMENT, ORDER1_PRICE)))
                .expectActiveSagas(1)
                .expectDispatchedCommands(new PrepareShipmentCommand(ORDER1_ID, new ProductInfo(ORDER1_PRODUCT_ID, ORDER1_COMMENT, ORDER1_PRICE)));
    }

    @Test
    public void testShipmentRequested() {
        fixture.givenAPublished(new OrderFiledEvent(ORDER1_ID, new ProductInfo(ORDER1_PRODUCT_ID, ORDER1_COMMENT, ORDER1_PRICE)))
                .whenPublishingA(new PrepareShipmentCommand(ORDER1_ID, new ProductInfo(ORDER1_PRODUCT_ID, ORDER1_COMMENT, ORDER1_PRICE)))
                .expectNoScheduledEvents()
                .expectNoDispatchedCommands();
    }

}