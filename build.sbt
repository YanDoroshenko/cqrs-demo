scalaVersion := "2.13.12"

val http4sVersion = "0.23.23"

resolvers ++=
  ("confluent" at "https://packages.confluent.io/maven/") ::
    Resolver.sonatypeRepo("releases") ::
    Resolver.typesafeRepo("releases") ::
    ("Atlassian Releases" at "https://maven.atlassian.com/public/") ::
    Nil

libraryDependencies ++=
  "org.typelevel" %% "cats-core" % "2.10.0" ::
    "org.typelevel" %% "cats-effect" % "3.5.2" ::
    "com.banno" %% "kafka4s" % "5.0.3" ::
    "com.banno" %% "kafka4s-avro4s" % "5.0.3" ::
    "org.http4s" %% "http4s-ember-server" % http4sVersion ::
    "org.http4s" %% "http4s-ember-client" % http4sVersion ::
    "org.http4s" %% "http4s-circe" % http4sVersion ::
    "org.http4s" %% "http4s-dsl" % http4sVersion ::
    "org.slf4j" % "slf4j-simple" % "2.0.9" ::
    Nil
