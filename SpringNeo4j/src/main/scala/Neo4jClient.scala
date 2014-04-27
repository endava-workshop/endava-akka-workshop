import akka.actor.ActorSystem

import java.net.URLEncoder
import java.util.Date
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

  import system.dispatcher

  // execution context for futures

  println(new Date())
  val client = sendReceive
  //  val query = "MATCH%20(b:SimpleURL)%20RETURN%2020%20LIMIT%2020"
  //  val query = "MATCH (b:SimpleURL) RETURN b LIMIT 200"
  val address = "ro.wikipedia.org"
  val status = "NOT_VISITED"
  //  val query = s"MATCH (a:DomainURL {address: '$address'})-[:`CONTAINS`]->(url {status: '$status'}) RETURN url SKIP 1000 LIMIT 1000"

  val queries = List(
//    "CREATE INDEX ON :SimpleURL(url)",
//    "CREATE INDEX ON :DomainURL(address)",
//    "MATCH (d: DomainURL) RETURN d.name, d.coolDownPeriod SKIP 1 LIMIT 10",
//    s"MATCH (a:DomainURL) RETURN count(a)",
//    s"MATCH (a:SimpleURL) RETURN count(a)",
//    s"MATCH (a:SimpleURL {status: 'VISITED'}) RETURN count(a)",
//    s":schema ls -l :SimpleURL",
//    s":schema ls -l :DomainURL",
    s"MATCH (a:SimpleURL {status: 'NOT_VISITED'}) RETURN count(a)",
//    s"MATCH (a:SimpleURL {status: 'FAILED'}) RETURN count(a)",
//    s"MATCH (a:SimpleURL {status: 'FAILED'}) RETURN a",
//    s"MATCH (a:DomainURL {address: '$address'})-[:`CONTAINS`]->(url {status: '$status'}) RETURN url LIMIT 1000",
    s"MATCH (a:DomainURL)-[:`CONTAINS`]->(url {status: '$status'}) RETURN distinct a.name LIMIT 1000",
//    s"MATCH (a:DomainURL {address: 'fa.wikipedia.org'})  RETURN a LIMIT 1000",
//    s"MATCH (a:DomainURL {address: 'fa.wikipedia.org'})-[:`CONTAINS`]->(url {status: '$status'}) RETURN url LIMIT 1000",
  ""
  )
  for (query <- queries if !query.isEmpty) {
    print(s"\n\n$query\n\n")
    //  val query = s"CREATE INDEX ON :SimpleURL(url)"
    //  val query = s"CREATE INDEX ON :DomainURL(address)"
    val response = {
      val url = "http://localhost:8080/query?q=" + URLEncoder.encode(query, "UTF-8")
      println(url)
      client(Get(url))
    }


    val res = Await.result(response, 1 minute)
    println(res.entity.asString)
  }
  system.shutdown()

}