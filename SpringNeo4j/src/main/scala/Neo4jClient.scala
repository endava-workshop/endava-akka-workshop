import akka.actor.ActorSystem

import java.net.URLEncoder
import scala.concurrent.Await
import spray.httpx.encoding.Gzip
import spray.client.pipelining._
import scala.concurrent.duration._
import spray.json.JsObject

/**
 * Sample code to retrieve a web page
 */
object Neo4jClient extends App {

  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  val client = sendReceive
//  val query = "MATCH%20(b:SimpleURL)%20RETURN%2020%20LIMIT%2020"
//  val query = "MATCH (b:SimpleURL) RETURN b LIMIT 200"
  val address = "ro.wikipedia.org"
  val status = "NOT_VISITED"
//  val query = s"MATCH (a:DomainURL {address: '$address'})-[:`CONTAINS`]->(url {status: '$status'}) RETURN url SKIP 1000 LIMIT 1000"
//  val query = s"MATCH (a:DomainURL) RETURN count(a)"
  val query = s"MATCH (a:SimpleURL) RETURN count(a)"
  val response = {
  val url = "http://localhost:8080/query?q=" + URLEncoder.encode(query, "UTF-8")
  println(url)
  client(Get(url))
}



  val res = Await.result(response, 1 minute)
  println(res.entity.asString)

  system.shutdown()

}