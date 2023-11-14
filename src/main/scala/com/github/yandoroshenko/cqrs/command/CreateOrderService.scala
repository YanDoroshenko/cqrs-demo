package com.github.yandoroshenko.cqrs.command

import cats.effect._
import cats.effect.std.Console
import cats.Monad
import cats.implicits.catsSyntaxFlatMapOps
import com.github.yandoroshenko.cqrs.model.{Order, OrderId}
import com.github.yandoroshenko.cqrs.model.Order.OrderSerializer
import com.github.yandoroshenko.cqrs.model.OrderId.OrderIdSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties

class CreateOrderService[F[_]: Monad: Async: Console](topicName: String, kafkaBootstrapServers: String) {
  def producerRecord(order: Order): ProducerRecord[OrderId, Order] =
    new ProducerRecord(
      topicName,
      order.orderId,
      order,
    )

  val producerResource: Resource[F, KafkaProducer[OrderId, Order]] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[OrderIdSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[OrderSerializer].getName)

    Resource.make(
      Monad[F].pure(
        new KafkaProducer[OrderId, Order](props)
      )
    )(_ => Monad[F].unit)
  }

  def createOrder(order: Order): F[Unit] =
    Console[F].println(s"Create order - order: $order") >>
      producerResource.use { p =>
        Monad[F].pure(
          p.send(producerRecord(order)).get()
        ) >>
          Monad[F].unit
      }
}
