package com.akka.ws.neo4j.rest

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.io.IO
import akka.routing.RoundRobinRouter
import spray.can.Http

// https://github.com/spray/spray/blob/master/examples/spray-can/simple-http-server/src/main/scala/spray/examples/FileUploadHandler.scala
object Main extends App {

  val port = 8080

  implicit val system = ActorSystem()
  val handler = {
      val sprayActorCreator: () ⇒ Actor = () ⇒ new PersistenceRestInterface() {
        // urlService // INFO: urlService will control storage type: Mongo vs Neo4J
      }
      system.actorOf(Props(sprayActorCreator).withRouter(new RoundRobinRouter(50)), name = "handler")
  }
  println( s"Binding to [$port]..." )
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)

}