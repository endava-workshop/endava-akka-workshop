import akka.actor.ActorSystem

import scala.concurrent.Await
import scala.concurrent.duration._
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._

/**
 * Sample code for REST client
 */
case class SimpleUrl_(name: String, url: String)
case class DomainUrl_(name: Option[String], address: Option[String], internalUrlSet: Option[Set[SimpleUrl_]])
object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val simpleUrlFormat = jsonFormat2(SimpleUrl_)
  implicit val domainUrlFormat = jsonFormat3(DomainUrl_)
}

object RESTClientExample extends App {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  import MyJsonProtocol._

  // example 1: send some data
  val data = DomainUrl_(Some("domain val"), Some("domain addr"), Some(Set()))
  val client = sendReceive // ~> unmarshal(SomeResponseClass)
  val response = client(Post("http://localhost:8080/domainURL/val/addr", data))
  val res = Await.result(response, 1 minute)
  println(s"res1: [$res]" )


  // example 2: receive some data
  val client2 = sendReceive ~> unmarshal[List[DomainUrl_]]
  val response2 = client2(Get("http://localhost:8080/domains"))
  val res2 = Await.result(response2, 1 minute)
  println(s"res2: [$res2]" )

  system.shutdown()

}

