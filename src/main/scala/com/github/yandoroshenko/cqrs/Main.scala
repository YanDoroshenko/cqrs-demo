package com.github.yandoroshenko.cqrs

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s._
import com.github.yandoroshenko.cqrs.command.{CreateOrderService, ProcessPaymentService}
import com.github.yandoroshenko.cqrs.model.{Order, OrderId}
import org.http4s._
import org.http4s.dsl._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

import scala.language.postfixOps
import scala.util.Random

object Main extends IOApp {

  val kafkaBootstrapServers = "http://127.0.0.1:9092"

  val orderTopic = "orders"
  val paymentTopic = "payments"


  val createOrderService: CreateOrderService[IO] = new CreateOrderService[IO](orderTopic, kafkaBootstrapServers)
  val processPaymentService: ProcessPaymentService[IO] = new ProcessPaymentService[IO](paymentTopic, kafkaBootstrapServers)

  override def run(args: List[String]): IO[ExitCode] = {
    val dsl = new Http4sDsl[IO] {}

    import dsl._

    val routes = HttpRoutes.of[IO] {
      case GET -> Root =>
        for {
          greeting <- IO.pure("Hello")
          resp <- Ok(greeting)
        } yield resp
      case POST -> Root =>
        val order = Order(OrderId(Random.nextInt(100).toString), Random.nextInt(100))
        createOrderService.createOrder(order) >>
          Ok()
    }

    (for {
      _ <- EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(Logger.httpApp(true, true)(routes.orNotFound))
        .build
    } yield ())
      .useForever
  }
}
