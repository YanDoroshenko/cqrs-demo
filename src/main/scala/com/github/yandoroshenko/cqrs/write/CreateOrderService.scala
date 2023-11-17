package com.github.yandoroshenko.cqrs.write

import cats.effect._
import com.github.yandoroshenko.cqrs.model.{Order, OrderId}
import com.github.yandoroshenko.cqrs.model.Order.OrderSerializer
import com.github.yandoroshenko.cqrs.model.OrderId.OrderIdSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties

class CreateOrderService(topicName: String, kafkaBootstrapServers: String) {
  def producerRecord(order: Order): ProducerRecord[OrderId, Order] =
    new ProducerRecord(
      topicName,
      order.orderId,
      order,
    )

  val producerResource: Resource[IO, KafkaProducer[OrderId, Order]] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[OrderIdSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[OrderSerializer].getName)

    Resource.make(
      IO(
        new KafkaProducer[OrderId, Order](props)
      )
    )(p => IO(p.close()))
  }

  def createOrder(order: Order): IO[Unit] =
    producerResource.use { p =>
      IO(p.send(producerRecord(order)).get()).void >> IO.println(s"Create order - order: $order")
    }
}
