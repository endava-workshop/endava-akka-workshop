package example

import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration._
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._

/**
 * Sample code for REST client
 */
case class SimpleUrl2(name: String, url: String)
case class DomainUrl2(name: String, address: String, internalUrlSet: Set[SimpleUrl2])

object MyJsonProtocol2 extends DefaultJsonProtocol {
  implicit val simpleUrlFormat = jsonFormat2(SimpleUrl2)
  implicit val domainUrlFormat = jsonFormat3(DomainUrl2)
}

object RESTClientExample extends App {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  import MyJsonProtocol2._

  // example 1: send some data
  val data = DomainUrl2("domain val", "domain addr", Set(SimpleUrl2("sss", "sasas")))
  val client = sendReceive // ~> unmarshal(SomeResponseClass)
  val response = client(Post("http://localhost:8080/domainURL/val2/addr2", data))
  val res = Await.result(response, 1 minute)
  println(s"res1: [$res]" )


  // example 2: receive some data
  val client2 = sendReceive ~> unmarshal[List[DomainUrl2]]
  val response2 = client2(Get("http://localhost:8080/domains"))
  val res2 = Await.result(response2, 1 minute)
  println(s"res2: [$res2]" )

  system.shutdown()

}

