package org.learn.axonframework.coreapi

class ShipmentRequestedEvent(val orderId: String, val productInfo: ProductInfo)

class ShipmentPreparedEvent(val shipmentId : String, val orderId: String, val shippingPrice : Int)