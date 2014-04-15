package main

import akka.io.IO
import spray.can.Http
import akka.actor.{Actor, Props, ActorSystem}
import akka.actor.ActorSystem
import rest.ESHttpService
import ro.endava.akka.workshop.actors.IndexDispatcherActor

object Main extends App {

  val port = 8080

  implicit val system = ActorSystem()
  val handler = {
      val actorCreator: () ⇒ Actor = () ⇒ new ESHttpService(system.actorOf(Props[IndexDispatcherActor])) {
      }
      system.actorOf(Props(actorCreator), name = "handler")
  }
  println( s"Binding to [$port]..." )
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)
}