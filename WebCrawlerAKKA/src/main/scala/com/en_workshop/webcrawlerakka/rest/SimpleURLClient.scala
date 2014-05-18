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

  //TODO update the SimpleUrl definition so it corresponds to the Link entity definition

  implicit val timeout: Duration = 3 minute

  lazy val addDomainLinkClient = sendReceive// ~> unmarshal[List[DomainLink_]])
  def addDomainLinks(domainLinks: java.util.Collection[DomainLinkDTO], sync: Boolean = false): Unit = {

      val f = timed(s"add ${domainLinks.size()} DomainLinks") {
        addDomainLinkClient(Post(s"$webRoot/domainLinks", domainLinks))
      }
      if (sync) {
        Await.result(f, timeout)
      }

  }

  lazy val getUrlClient = (sendReceive ~> unmarshal[List[LinkDTO]])
  def getURLs(domainAddress: String, status: String, pageNo: Int, pageSize: Int): java.util.List[Link] = {
    val f = timed("get URLs", domainAddress) {
      getUrlClient(Get(s"$webRoot/domain/$domainAddress/links?status=$status&pageNo=$pageNo&pageSize=$pageSize"))
    }
    // translate DTO to domain model
    val result = for (u <- Await.result(f, timeout))
      yield new Link(u.domain, u.url, null, LinkStatus.valueOf(u.status))
    println(s"found ${result.size} links for $domainAddress")
    result
  }

  lazy val urlBulkStatusClient = sendReceive
  def setURLsStatus(url: java.util.List[String], status: String): Unit = {
    timed(s"bulk update status for ${url.size()} links") {
      urlBulkStatusClient(Post(s"$webRoot/updateLinks", url.map(new LinkDTO("", _, status, ""))))
    }
  }

  lazy val flushClient = sendReceive
  def flush(): Unit = {
    val f = timed("flush") {
      flushClient(Get(s"$webRoot/flush"))
    }
    Await.result(f, timeout)
  }

}

case class LinkDTO(domain: String, url: String, status: String, sourceLink: String)
case class DomainLinkDTO(domain: DomainDTO, link: LinkDTO)

