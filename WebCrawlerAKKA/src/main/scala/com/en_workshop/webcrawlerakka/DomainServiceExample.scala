package com.en_workshop.webcrawlerakka

import _root_.akka.actor.ActorSystem

import org.json4s.{DefaultFormats, Formats}
import scala.concurrent.Await
import scala.concurrent.duration._
import spray.httpx.Json4sSupport
import spray.client.pipelining._

// TODO incomplete - WIP

/**
 * Sample code for REST client
 */
case class SimpleUrl_(url: String, name: Option[String], status: Option[String], errorCount: Option[Int]) {
  def this(url: String, name: Option[String], status: String) = this(url, name, Option(status), None)
}

case class DomainDTO(name: String, coolDownPeriod: Option[Long], crawledAt: Option[Long], domainStatus: Option[String])
case class LinkDTO(domain: String, url: String, status: String, sourceLink: String)
case class DomainLinkDTO(domain: DomainDTO, link: LinkDTO)
//case class DomainUrl_(address: String, name: Option[String], coolDownPeriod: Option[Long])

object DomainServiceExample extends App with Json4sSupport {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  implicit def json4sFormats: Formats = DefaultFormats

//  val clientX = sendReceive
//  clientX(Get("http://localhost:8080/purge"))

//  // example 1: send some data
//  val data = DomainUrl_("www.domain5.com", Some("domain5"), Some(2200))
//  val domainClient = sendReceive  ~> unmarshal[DomainUrl_]
//  val response = domainClient(Post("http://localhost:8080/domain", data))
//  val res = Await.result(response, 1 minute)
//  println(s"res1: [$res]" )
//
//
//  // example 2: receive some data
//  val domainClient = sendReceive  ~> unmarshal[List[Domain_]]
//  val response2 = domainClient(Get("http://localhost:8080/domain?pageNo=0&pageSize=10"))
//  val res2 = Await.result(response2, 1 minute)
//  println(s"res2: [$res2]" )
//
//  def showLinks(address: String) = {
//    val urlsClient = (sendReceive ~> unmarshal[List[SimpleUrl_]])
//    val response3 = urlsClient(Get(s"http://localhost:8080/domain/${address}/url?status=NOT_VISITED&pageNo=0&pageSize=100"))
//    val res3 = Await.result(response3, 1 minute)
//    println(s"LINKS : [$res3]")
//  }
//  showLinks("www.domain5.com");
//  {
//    var cnt = 0
//    for (k <- 1000 to 10000) {
//      val urlClient = sendReceive ~> unmarshal[SimpleUrl_]
//      for (i <- 1 to 10) {
//        cnt = cnt + 1
//        val t0 = System.currentTimeMillis()
//        val url = s"http://www.archeus.ro/lingvistica/CautareDex?query=VALOARE&seed=$cnt"
//        var response4 = urlClient(Post("http://localhost:8080/domain/www.domain5.com/url", new SimpleUrl_(url, Some(s"n $cnt"), "NOT_VISITED")))
//        val res4 = Await.result(response4, 1 minute)
//        val t1 = System.currentTimeMillis()
//        println(s"res3: [$res4] in ${t1 - t0}ms")
//        response4 = null
//      }
//    }
//  }

//  showLinks("www.domain5.com");
//  val urlClient = sendReceive
//  for (k <- 1 to 10)
//  {
//    val t0 = System.currentTimeMillis()
//    val url = "ura"
//    val status = "VISITED"
//    val response4 = urlClient(Patch(s"http://localhost:8080/url/status"))
//    val res4 = Await.result(response4, 1 minute)
//    val t1 = System.currentTimeMillis()
//    println(s"rest call took ${t1-t0}ms")
//    println(s"update res: [$res4] in ${t1-t0}")
//  }

//  showLinks("www.domain5.com");
//  {
//    val urlClient = sendReceive
//    val url = "ura"
//    val response4 = urlClient(Post(s"http://localhost:8080/url/$url/error"))
//    val res4 = Await.result(response4, 1 minute)
//    println(s"update res: [$res4]")
//  }
//  showLinks("www.domain5.com");
//  {
//    val urlClient = sendReceive
//    val url = "ura"
//    val response4 = urlClient(Post(s"http://localhost:8080/url/$url/error"))
//    val res4 = Await.result(response4, 1 minute)
//    println(s"update res: [$res4]")
//  }
//
//    // example 1: send some data
//    val data = DomainDTO("www.domain7.com", Some(2200), Some(2300), Some("EXHAUSTED"))
//    val domainClient = sendReceive
//    val response = domainClient(Put("http://localhost:8080/domain", data))
//    val res = Await.result(response, 1 minute)
//    println(s"res1: [$res]" )

  // example 1: send some data
  val domain = DomainDTO("www.domain1a.com", Some(2200), Some(2300), Some("FOUND"))
  val data = List(DomainLinkDTO(domain, LinkDTO("www.d1a.com", "page1a", "NOT_VISITED", "src1")),
                  DomainLinkDTO(domain, LinkDTO("www.d2a.com", "page2a", "NOT_VISITED", "src2")),
                  DomainLinkDTO(domain, LinkDTO("www.d2a.com", "page3a", "NOT_VISITED", "src2"))
  )
  val domainClient = sendReceive
  val response = domainClient(Post("http://localhost:8080/domainLinks", data))
  val res = Await.result(response, 1 minute)
  println(s"res1: [$res]" )

  val flushResponse = domainClient(Get("http://localhost:8080/flush"))
  val flushResult = Await.result(flushResponse, 1 minute)
  println(s"res1: [$flushResult]" )

  system.shutdown()

}
