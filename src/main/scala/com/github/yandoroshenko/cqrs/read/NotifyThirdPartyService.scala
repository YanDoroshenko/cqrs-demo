package com.github.yandoroshenko.cqrs.read

import cats.effect.IO
import cats.implicits._
import com.github.yandoroshenko.cqrs.model.OrderId
import com.github.yandoroshenko.cqrs.model.OrderId.OrderIdDeserializer
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

import java.util.Properties
import scala.jdk.CollectionConverters._

class NotifyThirdPartyService(topicName: String, kafkaBootstrapServers: String) {

  def register() = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[OrderIdDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[OrderIdDeserializer].getName)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "notify-third-party")
    props.put(ConsumerConfig.CLIENT_ID_CONFIG, "1")

    val c = new KafkaConsumer[OrderId, OrderId](props)
    c.subscribe(List(topicName).asJava)
    IO(c.poll(10000).records(topicName).iterator().asScala.toList)
  }
    .flatMap(records => records.map(r => IO.println(s"Third party notified: ${r.value()}")).sequence)
    .recoverWith {
      case ex =>
        IO.println(ex.getMessage) >>
          IO.raiseError(ex)
    }
    .foreverM
    .start
    .void
}
