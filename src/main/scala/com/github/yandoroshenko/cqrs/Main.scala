package com.github.yandoroshenko.cqrs

import cats.effect.{ExitCode, IO, IOApp}
import com.github.yandoroshenko.cqrs.dto.ReadDTO
import com.github.yandoroshenko.cqrs.model.Entity

import scala.language.postfixOps

object Main extends IOApp {

  var writeDB: List[Entity] = Nil
  var readDB: List[ReadDTO] = Nil

  var readService: () => List[ReadDTO] = () => readDB
  var writeService: Entity => Unit = (x: Entity) => {
    writeDB = writeDB :+ x
  }

  val syncDB = () => readDB = writeDB.map { entity =>
    ReadDTO(entity.requestId, entity.data, entity.data % 2 == 0)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val request = Entity("1", 1)

    for {
      res1 <- IO(readService())
      _ <- IO.println(s"Before insert: $res1")
      _ <- IO.println("Insert")
      _ <- IO(writeService(request))
      _ <- IO(syncDB())
      res2 <- IO(readService())
      _ <- IO.println(s"After insert: $res2")
    } yield ExitCode.Success
  }
}
