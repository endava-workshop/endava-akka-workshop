package com.akka.ws.neo4j.rest

import spray.routing._
import com.akka.ws.neo4j.service.PersistenceService
import com.akka.ws.neo4j.entity.{ DomainLink, Domain, Link }
import spray.httpx.Json4sSupport
import org.json4s.{ DefaultFormats, Formats }
import scala.concurrent.duration._
import akka.util.Timeout
import spray.http.HttpHeaders._
import spray.http.ContentTypes._
import scala.collection.JavaConversions._
import com.akka.ws.neo4j.service.PersistenceServiceImpl
import scala.collection.JavaConversions._
import com.akka.ws.neo4j.enums.LinkStatus

abstract class PersistenceRestInterface extends HttpServiceActor with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(30 seconds)
  //  lazy val urlService = springContext.getBean(classOf[MongoUrlServiceImpl])
  lazy val persistenceService = new PersistenceServiceImpl(true, 1500)

  def receive = runRoute {(
    path("purge") {
      complete {
        persistenceService.cleanDatabase()
        "Removed all domains"
      }
    } ~
      path("domain") {
        put { // CREATE Domain
          entity(as[Domain]) { domain =>
            val newDomain = persistenceService.addDomain(domain);
            print(s"Domain created: ${domain.getName()}")
            if (newDomain)
              complete(s"added new domain")
            else
              complete(s"domain already exists")
          }
        } ~
          get { // RETRIEVE Domains
            parameters('pageNo ? 0, 'pageSize ? 1000) { (pageNo: Int, pageSize: Int) =>
              val domains = persistenceService.getDomains(pageNo, pageSize)
              complete(domains)
            }
          }
      } ~
      path("domain" / Segment) { // DELETE Domains
        domainName =>
          delete {
            persistenceService.removeDomain(domainName)
            complete(s"OK")
          }
      } ~
      path("domain" / Segment / "links") { domainURL: String =>
        get { // RETRIEVE LINKS for a domain
          parameters('status, 'pageNo ? 0, 'pageSize ? 1000) { (status: String, pageNo: Int, pageSize: Int) =>
            val urls = persistenceService.getDomainLinks(domainURL, status, pageNo, pageSize)
            complete(urls)
          }
        }
      } ~
      path("domainLinks") {
        post {
          // CREATE LINKS
          entity(as[List[DomainLink]]) {
            domainLinks: List[DomainLink] =>
              val t0 = System.currentTimeMillis()
              persistenceService.addDomainLinks(domainLinks)
              val t1 = System.currentTimeMillis()
              /**
               * TODO - this is not relevant - the commit is done only after the batch size passes the minBatchSize value
               */
              println(s"bulk add ${domainLinks.size} urls in ${t1 - t0}ms")
              complete("OK")
          }
        }
      } ~
      path("updateLinks") { // UPDATE LINK Status - bulk operation
        post {
          entity(as[List[Link]]) { 
        	links: List[Link] =>
            val t0 = System.currentTimeMillis()
            persistenceService.updateLinks(links)
            val t1 = System.currentTimeMillis()
            println(s"bulk status update for ${links.size} urls in ${t1 - t0}ms")
            complete("OK")
          }
        }
      } ~
      /**
       * not sure if still used
       */
//      path("url" / "error") { // UPDATE LINK error count
//        patch {
//          entity(as[SimpleUrlError]) { patch =>
//            urlService.updateSimpleUrlErrorStatus(patch.url, patch.errorDelta)
//            complete("OK")
//          }
//        }
//      } ~
      path("url" / "ping") {
        complete {
          println("ping")
          "pong"
        }
      })
  }

}
