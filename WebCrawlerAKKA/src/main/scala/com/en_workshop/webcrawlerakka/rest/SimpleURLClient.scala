package com.en_workshop.webcrawlerakka.rest

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import spray.client.pipelining._
import com.en_workshop.webcrawlerakka.entities.Link
import com.en_workshop.webcrawlerakka.enums.LinkStatus
import scala.collection.JavaConversions._
import scala.collection.immutable.IndexedSeq
import scala.collection.mutable

/**
 * Created by ionut on 19.04.2014.
 */

object SimpleURLClient extends AbstractRestClient {

  lazy val addUrlClient = sendReceive
  def addURL(url: String, domain: String, sourceDomain: String): Unit = {
    timed("add URL", url) {
      val payload = new SimpleUrl_(url, Some(sourceDomain), Some(domain), Some(url), "NOT_VISITED")
      addUrlClient(Post(s"$webRoot/domain/$domain/url", payload))
    }
  }


  def addURLs(urls: java.util.Collection[SimpleUrl_]): Unit = {
    urls.flatMap(u => u.domain).toSet.foreach {
      domain: String => {
        val forCrtDomain = urls.filter(_.domain.getOrElse("") == domain)
        timed(s"add ${forCrtDomain.size()} URLs") {
          addUrlClient(Post(s"$webRoot/domain/${domain}/urls", forCrtDomain))
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
    for (u <- Await.result(f, 2 minute))
      yield new Link(u.sourceDomain.getOrElse(null), null, u.url, LinkStatus.valueOf(u.status.getOrElse(null)))
  }

  lazy val urlStatusClient = sendReceive
  def setURLStatus(url: String, status: String): Unit = {
    timed("status update", url) {
      urlStatusClient(Patch(s"$webRoot/url/status", SimpleUrlStatus(url, status)))
    }
  }

  lazy val urlErrorClient = sendReceive
  def markURLError(url: String): Unit = {
    timed("incrementing error cont", url){
      val payload = SimpleUrlError(url, 1)
      urlErrorClient(Post(s"$webRoot/url/error", payload))
    }
  }

}

case class SimpleUrl_(url: String, sourceDomain: Option[String], domain: Option[String], name: Option[String], status: Option[String], errorCount: Option[Int]) {
  def this(url: String, sourceDomain: Option[String], domain: Option[String], name: Option[String], status: String) = this(url, sourceDomain, domain, name, Option(status), None)
}
case class SimpleUrlStatus(url: String, status: String)
case class SimpleUrlError(url: String, errorDelta: Int)
