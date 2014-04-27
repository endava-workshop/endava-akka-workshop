package rest

import spray.routing._
import service.UrlService
import org.springframework.data.domain.PageRequest
import entity.{DomainLink, SimpleURL, DomainURL}
import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}


import scala.concurrent.duration._
import akka.util.Timeout
import spray.http.HttpHeaders._
import spray.http.ContentTypes._
import scala.collection.JavaConversions._
import service.impl.{UrlServiceImpl, MongoUrlServiceImpl}
import scala.collection.JavaConversions._

case class DomainLinkDTO(domain: DomainURL, link: SimpleURLDTO)
case class SimpleURLDTO(url: String, sourceDomain: Option[String], name: Option[String], status: Option[String], errorCount: Option[Int], lastUpdate: Option[Long])
case class SimpleUrlStatus(url: String, status: String)
case class SimpleUrlsStatus(urls: List[String], status: String)
case class SimpleUrlError(url: String, errorDelta: Int)
abstract class DomainURLService extends HttpServiceActor with ApplicationContextSupport  with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(30 seconds)
//  lazy val urlService = springContext.getBean(classOf[MongoUrlServiceImpl])
  lazy val urlService = springContext.getBean(classOf[UrlServiceImpl])

  def receive = runRoute {
      path("purge") {
        complete {
          urlService.removeDomains()
          "Removed all domains"
        }
      } ~
      path("domain") {
        post { // CREATE Domain
          entity(as[DomainURL]) { domainUrl =>
            val newDomainUrl = urlService.addDomainUrl(domainUrl.getName, domainUrl.getAddress, domainUrl.getCoolDownPeriod)
//            val newDomainUrl = new DomainURL()
            print(s"Domain created: ${domainUrl.getAddress}")
            complete(newDomainUrl)
          }
        } ~
        get { // RETRIEVE Domains
          parameters('pageNo ? 0, 'pageSize ? 1000) { (pageNo: Int, pageSize: Int) =>
            val domains = urlService.findDomains(new PageRequest(pageNo, pageSize))
            complete(domains)
          }
        }
      } ~
      path("domain" / Segment) { // DELETE Domains
        domainURL =>
          delete {
            urlService.removeDomainUrl(domainURL)
            complete(s"OK")
          }
      } ~
      path("domain" / Segment / "urls") {
        domainURL: String =>
          post {
            // CREATE LINK under a domain
            entity(as[List[SimpleURLDTO]]) {
              simpleUrls =>
                val t0 = System.currentTimeMillis()
                val sample = simpleUrls(0)
                val status = sample.status.getOrElse(null)
                val sourceDomain = sample.sourceDomain.getOrElse(null)
                urlService.addSimpleUrls(simpleUrls.map(_.url), status, domainURL, sourceDomain)
                val t1 = System.currentTimeMillis()
                println(s"bulk add in ${t1-t0}ms")
                complete("OK")
            }
          }
      } ~
      path("domain" / Segment / "url") { domainURL: String =>
        get { // RETRIEVE LINKS for a domain
          parameters('status, 'pageNo ? 0, 'pageSize ? 1000) { (status: String,  pageNo: Int, pageSize: Int) =>
              val urls = urlService.findURLs(domainURL, status, pageNo, pageSize)
              complete(urls.map(u => new SimpleURLDTO(u.getUrl, Some(domainURL), Option(u.getName), Option(u.getStatus), Option(u.getErrorCount), Option(u.getLastUpdate))))
          }
        }
      } ~
    // this is broken: if gets called for non-delete requests too!
//      path("url" / Segment) { url => // DELETE LINK
//        delete {
//          println("delete")
//          urlService.removeSimpleUrl(url)
//          complete(s"OK")
//        }
//      } ~
      path("urls") {
          post {
            // CREATE LINKS
            entity(as[List[DomainLinkDTO]]) {
              domainLinks: List[DomainLinkDTO] =>
                val t0 = System.currentTimeMillis()
                urlService.addDomainLinks(domainLinks.map(dto => new DomainLink(dto.domain, new SimpleURL(dto.link.url, dto.link.name.getOrElse(null), dto.link.status.getOrElse("NOT_VISITED")))))
                val t1 = System.currentTimeMillis()
                println(s"bulk add ${domainLinks.size} urls in ${t1-t0}ms")
                complete("OK")
            }
          }
      } ~
      path("url" / "status" ) { // UPDATE LINK Status - bulk operation
        patch {
          entity(as[SimpleUrlsStatus]) { patch =>
            val t0 = System.currentTimeMillis()
            urlService.updateSimpleUrlsStatus(patch.urls, patch.status)
            val t1 = System.currentTimeMillis()
            println(s"bulk status update for ${patch.urls.size} urls in ${t1-t0}ms")
            complete("OK")
          }
        }
      } ~
      path("url" / "error") { // UPDATE LINK error count
        patch {
          entity(as[SimpleUrlError]) {patch =>
            urlService.updateSimpleUrlErrorStatus(patch.url, patch.errorDelta)
            complete("OK")
          }
        }
      } ~
      path("url" / "ping") {
        complete {
          println("ping")
          "pong"
        }
      } ~
      path("query") {
        parameters('q) { (query: String) =>
          val result = urlService.query(query)
          complete(result)
        }
      }
  }

}
