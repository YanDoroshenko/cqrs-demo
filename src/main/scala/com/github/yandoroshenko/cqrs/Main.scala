package com.github.yandoroshenko.cqrs

import cats.effect.{ExitCode, IO, IOApp}
import com.github.yandoroshenko.cqrs.dto.ReadDTO
import com.github.yandoroshenko.cqrs.model.Entity

object Main extends IOApp {

  var db: List[Entity] = Nil

  var readService: () => List[ReadDTO] = () => db.map { entity =>
    ReadDTO(entity.requestId, entity.data, entity.data % 2 == 0)
  }

  var writeService: Entity => Unit = (x: Entity) => {
    db = db :+ x
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val request = Entity("1", 1)

    for {
      res1 <- IO(readService())
      _ <- IO.println(s"Before insert: $res1")
      _ <- IO.println("Insert")
      _ <- IO(writeService(request))
      res2 <- IO(readService())
      _ <- IO.println(s"After insert: $res2")
    } yield ExitCode.Success
  }
}
