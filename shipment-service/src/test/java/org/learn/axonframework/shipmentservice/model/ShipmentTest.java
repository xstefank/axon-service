package org.learn.axonframework.shipmentservice.model;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.learn.axonframework.coreapi.ShipmentPreparedEvent;

public class ShipmentTest {

    private AggregateTestFixture<Shipment> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Shipment.class);
    }

    @Test
    @Ignore
    public void testShipmentCreated() throws Exception {
        fixture.givenNoPriorActivity()
                .when(new PrepareShipmentCommand("1111", "2222", 100))
                .expectEvents(new ShipmentPreparedEvent("1111", "2222", 100));
    }
}