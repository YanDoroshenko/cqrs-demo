package com.github.yandoroshenko.cqrs.command

import cats.effect._
import cats.effect.std.Console
import cats.Monad
import cats.implicits.catsSyntaxFlatMapOps
import com.banno.kafka._
import com.banno.kafka.avro4s._
import com.banno.kafka.producer._
import com.github.yandoroshenko.cqrs.model.Order.OrderSerializer
import com.github.yandoroshenko.cqrs.model.{Order, OrderId}
import com.github.yandoroshenko.cqrs.model.OrderId.OrderIdSerializer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.util.Properties

class ProcessPaymentService[F[_]: Monad: Async: Console](topicName: String, kafkaBootstrapServers: String) {
  def producerRecord(orderId: OrderId): ProducerRecord[OrderId, OrderId] =
    new ProducerRecord(
      topicName,
      orderId,
      orderId
    )

  val producerResource: Resource[F, KafkaProducer[OrderId, OrderId]] = {
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[OrderIdSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[OrderIdSerializer].getName)

    Resource.make(
      Monad[F].pure(
        new KafkaProducer[OrderId, OrderId](props)
      )
    )(_ => Monad[F].unit)
  }

  def processPayment(orderId: OrderId): F[Unit] =
    Console[F].println(s"Process payment - orderId: $orderId") >>
      producerResource.use { p =>
        Monad[F].pure(
          p.send(producerRecord(orderId)).get()
        ) >>
          Monad[F].unit
      }
}
