package com.en_workshop.webcrawlerakka.rest

import org.json4s.Formats
import scala.concurrent.Await
import scala.concurrent.duration._
import spray.client.pipelining._
import com.en_workshop.webcrawlerakka.entities.Domain
import scala.collection.JavaConversions._
import com.en_workshop.webcrawlerakka.WebCrawlerConstants

/**
  * Created by ionut on 19.04.2014.
  */
object DomainURLClient extends AbstractRestClient {

  lazy val addDomainClient = sendReceive
  def addDomain(address: String, name: String, coolDownPeriod: Long): Unit = {
    timed(s"add domain [$name]", address) {
      val payload = buildDomainUrl(address, name, coolDownPeriod)
      addDomainClient(Post(s"$webRoot/domain", payload))
    }
  }


  def buildDomainUrl(address: String, name: String, coolDownPeriod: Long): DomainUrl_ = {
    new DomainUrl_(address, Option(name), Some(coolDownPeriod))
  }

  lazy val listDomainsClient = (sendReceive ~> unmarshal[List[DomainUrl_]])
  def listDomains(pageNo: Int, pageSize: Int): java.util.List[Domain] = {
    // get the list of domains (DTO)
    val f = timed("list domains") {
      listDomainsClient(Get(s"$webRoot/domain?pageNo=$pageNo&pageSize=$pageSize"))
    }
    // translate DTO to domain model
    var result = (for (d <- Await.result(f, 1 minute))
//      yield new Domain(d.name.getOrElse(null), d.coolDownPeriod.getOrElse(WebCrawlerConstants.DOMAIN_DEFAULT_COOLDOWN), 0))
      yield new Domain(d.name.getOrElse(null), 1000, 0))
//      yield new Domain(d.name.getOrElse(null), 0, 0))

    // filter
    val filter: String = com.en_workshop.webcrawlerakka.WebCrawler.DOMAIN_NAME_FILTER
    result = result.filter(domainUrl => (domainUrl.getName.contains(filter) || (filter == "*")))
    println(s"found ${result.size} domains")
    result
  }

}

case class DomainUrl_(address: String, name: Option[String], coolDownPeriod: Option[Long])
