package example

import akka.actor.ActorSystem

import scala.concurrent.Await
import spray.httpx.encoding.Gzip
import spray.client.pipelining._
import scala.concurrent.duration._

/**
 * Sample code to retrieve a web page
 */
object ClientExample extends App {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  val client = sendReceive ~> decode(Gzip)
  val response = client(Get("http://spray.io/"))


  val res = Await.result(response, 1 minute)
  println(res)

  system.shutdown()

}