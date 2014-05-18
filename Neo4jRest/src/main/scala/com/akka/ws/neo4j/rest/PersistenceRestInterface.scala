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
import com.akka.ws.neo4j.enums.{DomainStatus, LinkStatus}

case class DomainDTO(name: String, coolDownPeriod: Option[Long], crawledAt: Option[Long], domainStatus: Option[String])
case class LinkDTO(domain: String, url: String, status: String, sourceLink: String)
case class DomainLinkDTO(domain: DomainDTO, link: LinkDTO)


object DTOImplicits {
  implicit def domainFromDTO(dto: DomainDTO) = new Domain(dto.name, dto.coolDownPeriod.getOrElse(0), dto.crawledAt.getOrElse(0), DomainStatus.valueOf(dto.domainStatus.getOrElse("FOUND")))
  implicit def domainToDTO(dto: Domain) = DomainDTO(dto.getName(), Some(dto.getCoolDownPeriod), Some(dto.getCrawledAt), Some(dto.getDomainStatus.toString))
  
  implicit def linkFromDTO(dto: LinkDTO) = new Link(dto.domain, dto.url, dto.sourceLink, LinkStatus.valueOf(dto.status))
  implicit def linkFromDTOList(dtos: List[LinkDTO]) = dtos.map(linkFromDTO)
  implicit def linkToDTO(model: Link) = LinkDTO(model.getDomain, model.getUrl, model.getStatus.toString, model.getSourceLink)

  implicit def domainLinkFromDTO(dto: DomainLinkDTO) = new DomainLink(dto.domain, dto.link)
  implicit def domainLinkFromDTOList(dtos: List[DomainLinkDTO]): List[DomainLink] = dtos.map(domainLinkFromDTO)
  implicit def domainLinkToDTO(model: DomainLink) = DomainLinkDTO(model.getDomain, model.getLink)
}

abstract class PersistenceRestInterface extends HttpServiceActor with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats
  import DTOImplicits._

  implicit val timeout = Timeout(30 seconds)
  //  lazy val urlService = springContext.getBean(classOf[MongoUrlServiceImpl])
  lazy val persistenceService = new PersistenceServiceImpl(true, 1500)

  def receive = runRoute {
    (
      path("purge") {
        complete {
          persistenceService.cleanDatabase()
          "Removed all domains"
        }
      } ~
      path("domain") {
        put { // CREATE Domain
          entity(as[DomainDTO]) { domain =>
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
            complete(domains.map(domainToDTO))
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
            complete(urls.map(linkToDTO))
          }
        }
      } ~
      path("domainLinks") {
        post {
          // CREATE LINKS
          entity(as[List[DomainLinkDTO]]) {
            domainLinks: List[DomainLinkDTO] =>
              val t0 = System.currentTimeMillis()
              val model: List[DomainLink] = domainLinks
              persistenceService.addDomainLinks(model)
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
          entity(as[List[LinkDTO]]) {
            links: List[LinkDTO] =>
              val t0 = System.currentTimeMillis()
              val domain: List[Link] = links
              persistenceService.updateLinks(domain)
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
      path("flush") {
        complete {
          persistenceService.flush()
          "'flush' DONE"
        }
      } ~
      path("url" / "ping") {
        complete {
          println("ping")
          "pong"
        }
      })
  }

}
