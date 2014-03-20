package rest
import scala.collection.JavaConversions._
import spray.routing._
import scala.concurrent.duration.Duration
import service.UrlService
import org.springframework.data.domain.{PageRequest, Page}
import entity.DomainUrl


// magic import

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import spray.httpx.marshalling._

import scala.concurrent.duration._
import akka.util.Timeout
import spray.util._
import spray.http._


abstract class Neo4JHttpService extends HttpServiceActor with ApplicationContextSupport   {

  implicit val timeout = Timeout(30 seconds)
//  val urlService = () => springContext.getBean(classOf[UrlService])
  lazy val urlService = springContext.getBean(classOf[UrlService])

  def receive = runRoute {
    path("purge") {
      complete {
        urlService.removeAllDomains()
        "Removed all domains"
      }
    } ~
    path("domainURL" / Segment / Segment) { (domainName: String, domainURL: String) =>
      post {
        urlService.addDomainUrl(domainName, domainURL)
        complete(s"Added $domainName - $domainURL")
      }
    } ~
    path("domainURL" / Segment) { domainURL =>
      delete {
        urlService.removeDomainUrl(domainURL)
        complete(s"Removed $domainURL")
      }
    } ~
    path("simpleURL" / Segment / Segment/ Segment) { (domainName: String, simpleURL: String, name: String) =>
      post {
        urlService.addSimpleUrl(name, simpleURL, domainName)
        complete(s"Added $domainName - $simpleURL - $name")
      }
    } ~
    path("simpleURL" / Segment) { simpleURL =>
      delete {
        urlService.removeSimpleUrl(simpleURL)
        complete(s"Removed $simpleURL")
      }
    } ~
    path("domains") {
      get {
        complete(urlService.findDomains(new PageRequest(0, 1000)))
      }
    } ~
    path("stop") {
      complete {
        in(1.second){ actorSystem.shutdown() }
        "Shutting down in 1 second..."
      }
    }
  }

  def in[U](duration: FiniteDuration)(body: => U): Unit =
    actorSystem.scheduler.scheduleOnce(duration)(body)


//  implicit def futureMarshaller[T](implicit m: Marshaller[T], ec: ExecutionContext) =
//    Marshaller[Future[T]] { (value, ctx) ⇒
//      value.onComplete {
//        case Success(v)     ⇒ m(v, ctx)
//        case Failure(error) ⇒ ctx.handleError(error)
//      }
//    }
implicit val domainUrlMarshaller: Marshaller[Page[DomainUrl]] =
  Marshaller.delegate[Page[DomainUrl], String](ContentTypes.`text/plain`) { domainUrls: Page[DomainUrl] =>
//    domainUrls.getContent().flatMap(domainUrl => "address : " + domainUrl.getAddress)
    val x = domainUrls.getContent().flatMap(domainUrl => s"address: " + domainUrl.getAddress + "\n")
    x.mkString
  }
}
