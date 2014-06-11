package akka.ws.pass.rest

import akka.actor.ActorSystem

/**
 * Sample code to retrieve a web page
 */
object RestClientTest extends App {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  
  val res = RestClient.getPasswords(system, 10, 100)
  println(res)

  system.shutdown()

}