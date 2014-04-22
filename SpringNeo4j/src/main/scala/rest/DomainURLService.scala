package rest

import spray.routing._
import service.UrlService
import org.springframework.data.domain.PageRequest
import entity.{SimpleURL, DomainURL}
import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}


import scala.concurrent.duration._
import akka.util.Timeout
import spray.http.HttpHeaders._
import spray.http.ContentTypes._
import scala.collection.JavaConversions._

case class SimpleURLDTO(url: String, sourceDomain: Option[String], name: Option[String], status: Option[String], errorCount: Option[Int], lastUpdate: Option[Long])
case class SimpleUrlStatus(url: String, status: String)
case class SimpleUrlError(url: String, errorDelta: Int)
abstract class DomainURLService extends HttpServiceActor with ApplicationContextSupport  with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(30 seconds)
  lazy val urlService = springContext.getBean(classOf[UrlService])

  def receive = runRoute {
      path("purge") {
        complete {
          urlService.removeAllDomains()
          "Removed all domains"
        }
      } ~
      path("domain") {
        post { // CREATE Domain
          entity(as[DomainURL]) { domainUrl =>
            val newDomainUrl = urlService.addDomainUrl(domainUrl.getName, domainUrl.getAddress, domainUrl.getCoolDownPeriod)
//            val newDomainUrl = new DomainURL()
            complete(newDomainUrl)
          }
        } ~
        get { // RETRIEVE Domains
          parameters('pageNo ? 0, 'pageSize ? 1000) { (pageNo: Int, pageSize: Int) =>
            val domains = urlService.findDomains(new PageRequest(pageNo, pageSize)).getContent
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
        post {  // CREATE LINK under a domain
          entity(as[SimpleURLDTO]) { simpleUrl =>
            val name = simpleUrl.name.getOrElse(null)
            val status = simpleUrl.status.getOrElse(null)
            val sourceDomain = simpleUrl.sourceDomain.getOrElse(null)
            val url = urlService.addSimpleUrl(name, simpleUrl.url, status, domainURL, sourceDomain)
//            val url = new SimpleURL()
            complete(url)
          }
        } ~
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
      path("url" / "status" ) { // UPDATE LINK Status
        patch {
          entity(as[SimpleUrlStatus]) { patch =>
            urlService.updateSimpleUrlStatus(patch.url, patch.status)
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
