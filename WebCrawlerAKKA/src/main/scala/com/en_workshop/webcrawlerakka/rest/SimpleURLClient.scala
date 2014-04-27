package com.en_workshop.webcrawlerakka.rest

import scala.concurrent.Await
import scala.concurrent.duration._
import spray.client.pipelining._
import com.en_workshop.webcrawlerakka.entities.Link
import com.en_workshop.webcrawlerakka.enums.LinkStatus
import scala.collection.JavaConversions._

/**
 * Created by ionut on 19.04.2014.
 */

object SimpleURLClient extends AbstractRestClient {

  implicit val timeout: Duration = 3 minute
  lazy val addUrlClient = sendReceive
  def addURL(url: String, domain: String, sourceDomain: String): Unit = {
    timed("add URL", url) {
      val payload = new SimpleUrl_(url, Some(sourceDomain), Some(domain), Some(url), "NOT_VISITED")
      addUrlClient(Post(s"$webRoot/domain/$domain/url", payload))
    }
  }


  lazy val addDomainLinkClient = sendReceive// ~> unmarshal[List[DomainLink_]])
  def addDomainLinks(domainLinks: java.util.Collection[DomainLink_], sync: Boolean = false): Unit = {

      val f = timed(s"add ${domainLinks.size()} DomainLinks") {
        addDomainLinkClient(Post(s"$webRoot/urls", domainLinks))
      }
      if (sync) {
        Await.result(f, timeout)
      }

  }

  def addURLs(urls: java.util.Collection[SimpleUrl_], sync: Boolean = false): Unit = {
    urls.flatMap(u => u.domain).toSet.foreach {
      domain: String => {
        val forCrtDomain = urls.filter(_.domain.getOrElse("") == domain)
        val f = timed(s"add ${forCrtDomain.size()} URLs") {
          addUrlClient(Post(s"$webRoot/domain/${domain}/urls", forCrtDomain))
        }
        if (sync) {
          Await.result(f, timeout)
        }
      }
    }
  }

  lazy val getUrlClient = (sendReceive ~> unmarshal[List[SimpleUrl_]])
  def getURLs(domainAddress: String, status: String, pageNo: Int, pageSize: Int): java.util.List[Link] = {
    val f = timed("get URLs", domainAddress) {
      getUrlClient(Get(s"$webRoot/domain/$domainAddress/url?status=$status&pageNo=$pageNo&pageSize=$pageSize"))
    }
    // translate DTO to domain model
    val result = for (u <- Await.result(f, timeout))
      yield new Link(u.sourceDomain.getOrElse(null), null, u.url, null, LinkStatus.valueOf(u.status.getOrElse(null)))
    println(s"found ${result.size} links for $domainAddress")
    result
  }

  lazy val urlBulkStatusClient = sendReceive
  def setURLsStatus(url: java.util.List[String], status: String): Unit = {
    timed(s"bulk update status for ${url.size()} links") {
      urlBulkStatusClient(Patch(s"$webRoot/url/status", SimpleUrlsStatus(url.toList, status)))
    }
  }

  lazy val urlErrorClient = sendReceive
  def markURLError(url: String): Unit = {
    timed("incrementing error count", url){
      val payload = SimpleUrlError(url, 1)
      urlErrorClient(Patch(s"$webRoot/url/error", payload))
    }
  }

}

case class SimpleUrl_(url: String, sourceDomain: Option[String], domain: Option[String], name: Option[String], status: Option[String], errorCount: Option[Int]) {
  def this(url: String, sourceDomain: Option[String], domain: Option[String], name: Option[String], status: String) = this(url, sourceDomain, domain, name, Option(status), None)
}
case class DomainLink_(domain: DomainUrl_, link: SimpleUrl_)
case class SimpleUrlStatus(url: String, status: String)
case class SimpleUrlsStatus(urls: List[String], status: String)
case class SimpleUrlError(url: String, errorDelta: Int)
