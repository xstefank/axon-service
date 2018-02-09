package org.learn.axonframework.coreapi

import org.axonframework.commandhandling.TargetAggregateIdentifier

//model
data class ProductInfo(val productId: String = "", val comment: String = "", val price: Int = 0)


class OrderFiledEvent(val orderId: String, val productInfo : ProductInfo)
class OrderCompletedEvent(val orderId: String, val productInfo: ProductInfo)
class OrderCancelledEvent(val orderId: String)


class PrepareShipmentCommand(@TargetAggregateIdentifier val orderId: String, val productInfo: ProductInfo)
class ShipmentPreparedEvent(val shipmentId: String, val orderId: String, val price: Int)
class ShipmentPreparationFailedEvent(val shipmentId: String, val orderId: String, val cause: String)


class PrepareInvoiceCommand(@TargetAggregateIdentifier val orderId: String, val productInfo: ProductInfo)
class InvoicePreparedEvent(val invoiceId: String, val orderId: String, val invoice: String)
class InvoicePreparationFailedEvent(val invoiceId: String, val orderId: String, val cause: String)


//compensation

class CompensateShipmentCommand(@TargetAggregateIdentifier val orderId: String, val cause: String)
class ShipmentCompensatedEvent(val shipmentId: String, val orderId: String, val description: String)


class CompensateInvoiceCommand(@TargetAggregateIdentifier val orderId: String, val cause: String)
class InvoiceCompensatedEvent(val invoiceId: String ,val orderId: String, val description: String)
