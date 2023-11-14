package com.github.yandoroshenko.cqrs

import cats.effect.{ExitCode, IO, IOApp}
import com.github.yandoroshenko.cqrs.dto.OrderDTO
import com.github.yandoroshenko.cqrs.model.Order

import scala.language.postfixOps

object Main extends IOApp {

  var writeDB: List[Order] = Nil
  var readDB: List[OrderDTO] = Nil
  val paymentProcessor: IO[Unit] = IO.println("Payment processed")
  val emailSender: IO[Unit] = IO.println("Email sent")
  val reporting: IO[Unit] = IO.println("Order reported")
  val taxReporter: IO[Unit] = IO.println("Taxes reported")

  var readService: () => List[OrderDTO] = () => readDB
  var writeService: Order => Unit = (x: Order) => {
    writeDB = writeDB :+ x
  }

  val processInsert = IO {
    readDB = writeDB.map(OrderDTO)
  } >> IO.println("Read DB updated") >> paymentProcessor >> emailSender >> reporting >> taxReporter

  override def run(args: List[String]): IO[ExitCode] = {
    val request = Order("1", 1)

    for {
      res1 <- IO(readService())
      _ <- IO.println(s"Before insert: $res1")
      _ <- IO.println("Insert")
      _ <- IO(writeService(request))
      _ <- processInsert
      res2 <- IO(readService())
      _ <- IO.println(s"After insert: $res2")
    } yield ExitCode.Success
  }
}
