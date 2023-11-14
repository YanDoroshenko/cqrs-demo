package com.github.yandoroshenko.cqrs.model

import org.apache.kafka.common.serialization.Serializer

case class OrderId(id: String)

object OrderId {
  class OrderIdSerializer extends Serializer[OrderId] {
    override def serialize(topic: String, data: OrderId): Array[Byte] = data.id.getBytes()
  }
}

case class Order(orderId: OrderId, total: Double)

object Order {
  class OrderSerializer extends Serializer[Order] {
    override def serialize(topic: String, data: Order): Array[Byte] =
      data.toString.getBytes()
  }
}
