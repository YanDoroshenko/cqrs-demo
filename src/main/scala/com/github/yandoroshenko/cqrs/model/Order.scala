package com.github.yandoroshenko.cqrs.model

import org.apache.kafka.common.serialization.{Deserializer, Serializer}

case class OrderId(id: String)

object OrderId {
  class OrderIdSerializer extends Serializer[OrderId] {
    override def serialize(topic: String, data: OrderId): Array[Byte] = data.id.getBytes
  }

  class OrderIdDeserializer extends Deserializer[OrderId] {
    override def deserialize(topic: String, data: Array[Byte]): OrderId = OrderId(new String(data))
  }
}

case class Order(orderId: OrderId, total: Double)

object Order {
  class OrderSerializer extends Serializer[Order] {
    override def serialize(topic: String, data: Order): Array[Byte] =
      s"${data.orderId}x${data.total}".getBytes
  }

  class OrderDeserializer extends Deserializer[Order] {
    override def deserialize(topic: String, data: Array[Byte]): Order =
      new String(data).split("x").toList match {
        case id :: total :: Nil => Order(OrderId(id), total.toDouble)
      }
  }
}
