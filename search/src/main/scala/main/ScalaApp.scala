package main

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.io.IO
import akka.routing.RoundRobinRouter
import spray.can.Http
import rest.ESHttpService
import ro.endava.akka.workshop.actors.IndexDispatcherActor
import ro.endava.akka.workshop.messages.LocalPasswordMessage

object ScalaApp extends App {

  val port = 8080

  implicit val system = ActorSystem("SearchAkkaSystem")

  val dispatcherActor = system.actorOf(Props[IndexDispatcherActor])
  dispatcherActor.tell(new LocalPasswordMessage(
                "/common_passwords.txt", 10000), ActorRef.noSender);
  
  val handler = {
    val actorCreator: () ⇒ Actor = () ⇒ new ESHttpService(system){
    }
    system.actorOf(Props(actorCreator), name = "handler")
  }

  println(s"Binding to [$port]...")
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)

}