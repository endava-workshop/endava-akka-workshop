package main

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import rest.ESHttpService
import ro.endava.akka.workshop.actors.SearchRouterActor
import spray.can.Http
import ro.endava.akka.workshop.actors.IndexDispatcherActor
import ro.endava.akka.workshop.messages.LocalPasswordMessage

object ScalaApp extends App {

  val port = 8080

  implicit val system = ActorSystem("SearchAkkaSystem")

  val dispatcherActor = system.actorOf(Props[IndexDispatcherActor])
  dispatcherActor.tell(new LocalPasswordMessage(
                "/common_passwords.txt", 10000), ActorRef.noSender);
  
  val handler = {
    val actorCreator: () ⇒ Actor = () ⇒ new ESHttpService(system.actorOf(Props[SearchRouterActor])) {
    }
    system.actorOf(Props(actorCreator), name = "handler")
  }

  println(s"Binding to [$port]...")
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)

}