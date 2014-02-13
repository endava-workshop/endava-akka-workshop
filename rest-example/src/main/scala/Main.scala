package scala
// https://github.com/spray/spray/blob/master/examples/spray-can/simple-http-server/src/main/scala/spray/examples/FileUploadHandler.scala

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Main extends App {

  println( "Creating [ActorSystem]..." )

  implicit val system = ActorSystem()

  println( "Creating [DemoService]..." )

  // the handler actor replies to incoming HttpRequests
//  val handler = system.actorOf(Props[DemoService], name = "handler")
  val handler = system.actorOf(Props[DemoHttpService], name = "handler")

  val port = 8080

  println( s"Binding to [$port]..." )

  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)

  println( "Server started." )

//  println( "Stopping...." )
//  system.shutdown();
//  println( "Stopped" )
}