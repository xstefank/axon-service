package org.learn.axonframework.shipmentservice.model;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;

public class ShipmentTest {

    private AggregateTestFixture<Shipment> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(Shipment.class);
    }

}