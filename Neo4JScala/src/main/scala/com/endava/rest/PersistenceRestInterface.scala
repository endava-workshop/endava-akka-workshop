package com.endava.rest

import scala.collection.JavaConversions._
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import org.json4s.DefaultFormats
import org.json4s.Formats
import com.endava.command.dto.Domain
import akka.util.Timeout
import spray.http.ContentTypes._
import spray.http.HttpHeaders._
import spray.httpx.Json4sSupport
import spray.routing._
import com.endava.command.CommandService
import com.endava.command.dto.DomainLink

class PersistenceRestInterface extends HttpServiceActor with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(30 seconds)

  def receive = runRoute {
    (
      path("addDomain") {
        post{
          entity(as[Domain]){ domain =>
            val response:Boolean  = CommandService addDomain(domain);
          if (response)
              complete(s"added new domain")
            else
              complete(s"domain already exists")
          }
        }
      } ~
      path("addDomainLinks") {
        post{
          entity(as[List[DomainLink]]){ domainLinkList =>
            val response:Boolean  = CommandService addDomainLinks(domainLinkList);
          if (response)
              complete(s"added new domain")
            else
              complete(s"domain already exists")
          }
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
