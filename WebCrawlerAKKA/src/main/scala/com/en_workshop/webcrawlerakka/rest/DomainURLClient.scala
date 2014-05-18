package com.en_workshop.webcrawlerakka.rest

import scala.concurrent.Await
import scala.concurrent.duration._
import spray.client.pipelining._
import com.en_workshop.webcrawlerakka.entities.Domain
import scala.collection.JavaConversions._
import com.en_workshop.webcrawlerakka.enums.DomainStatus

/**
  * Created by ionut on 19.04.2014.
  */
object DomainURLClient extends AbstractRestClient {

  lazy val addDomainClient = sendReceive
  def addDomain(name: String, coolDownPeriod: Long): Unit = {
    timed(s"add domain [$name]", name) {
      val payload = buildDomainUrl(name, coolDownPeriod)
      addDomainClient(Put(s"$webRoot/domain", payload))
    }
  }

  def buildDomainUrl(name: String, coolDownPeriod: Long): DomainDTO = {
    new DomainDTO(name, Some(coolDownPeriod), Some(System.currentTimeMillis()), Some("FOUND"))
  }

  lazy val listDomainsClient = (sendReceive ~> unmarshal[List[DomainDTO]])
  def listDomains(pageNo: Int, pageSize: Int): java.util.List[Domain] = {
    // get the list of domains (DTO)
    val f = timed("list domains") {
      listDomainsClient(Get(s"$webRoot/domain?pageNo=$pageNo&pageSize=$pageSize"))
    }
    var result = (for (d <- Await.result(f, 1 minute))
//      yield new Domain(d.name.getOrElse(null), d.coolDownPeriod.getOrElse(WebCrawlerConstants.DOMAIN_DEFAULT_COOLDOWN), 0))
      yield new Domain(d.name, 1000, d.crawledAt.getOrElse(0)))

    // filter
    val filter: String = com.en_workshop.webcrawlerakka.WebCrawler.DOMAIN_NAME_FILTER
    result = result.filter(domainUrl => (domainUrl.getName.contains(filter) || (filter == "*")))
    println(s"found ${result.size} domains")
    result
  }

  def filterDomains(statuses: java.util.List[DomainStatus], pageNo: Int, pageSize: Int): java.util.List[Domain] = {
    listDomains(pageNo, pageSize) // TODO filter domains
  }
}

case class DomainDTO(name: String, coolDownPeriod: Option[Long], crawledAt: Option[Long], domainStatus: Option[String])
