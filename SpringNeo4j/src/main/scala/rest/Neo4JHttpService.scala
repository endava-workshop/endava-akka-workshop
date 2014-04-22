package rest
import scala.collection.JavaConversions._
import spray.routing._
import scala.concurrent.duration.Duration
import service.UrlService
import org.springframework.data.domain.{PageRequest, Page}
import entity.{SimpleURL, DomainURL}
import java.util
import com.gettyimages.spray.swagger.SwaggerApiBuilder
import scala.reflect.runtime.universe._
import com.wordnik.swagger.annotations.{ApiModelProperty, ApiOperation, ApiModel, Api}
import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}
import scala.annotation.meta.field


// magic import

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

import spray.httpx.marshalling._

import scala.concurrent.duration._
import akka.util.Timeout
import spray.util._
import spray.http._

// TODO incomplete - WIP

@Api(value = "/", description = "This is a Neo4J endpoint.")
abstract class Neo4JHttpService extends HttpServiceActor with ApplicationContextSupport  with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(30 seconds)
  //  val urlService = () => springContext.getBean(classOf[UrlService])
  lazy val urlService = springContext.getBean(classOf[UrlService])
  val swaggerApi = new SwaggerApiBuilder("1.2", "1.0", "swagger-specs", _: Seq[Type], _: Seq[Type])

//  val apiSwaggerResource = new Swagger
//  val apiScrapeUrlResource = new ScrapeUrl
//  val swaggerUIResource = new SwaggerUI
//
//  /**
//   * Define the [[receive]] of this actor as the return of [[runRoute]],
//   * passing in a route composed over routes obtained via extending various
//   * [[spray.routing.HttpService]]s that actually define those routes
//   */
//  def receive = runRoute(
//    apiScrapeUrlResource.routes ~
//      apiSwaggerResource.routes ~
//      swaggerUIResource.routes
//  )


  @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET") // TODO this needs to be moved at method level
  def receive = runRoute {
    path("purge") {
      complete {
        urlService.removeAllDomains()
        "Removed all domains"
      }
    } ~
      path("domainURL" / Segment / Segment) {
        (domainName: String, domainURL: String) =>
          post {
            urlService.addDomainUrl(domainName, domainURL, 1000)
            complete(s"Added $domainName - $domainURL")
          }
      } ~
      path("domainURL") {
          post {
            entity(as[DomainURL]) { domainUrl =>
              someObject =>
                val url = urlService.addDomainUrl(domainUrl.getName, domainUrl.getAddress, new java.lang.Long(domainUrl.getCoolDownPeriod))
                complete(url)
            }
          }
      } ~
      path("domainURL" / Segment) {
        domainURL =>
          delete {
            urlService.removeDomainUrl(domainURL)
            complete(s"Removed $domainURL")
          }
      } ~
      path("simpleURL" / Segment / Segment / Segment) {
        (domainURL: String, simpleURL: String, name: String) =>
          post {
            urlService.addSimpleUrl(name, simpleURL, "NOT_VISITED", domainURL, null)
            complete(s"Added $domainURL - $simpleURL - $name")
          }
      } ~
      path("simpleURL" / Segment) {
        simpleURL =>
          delete {
            urlService.removeSimpleUrl(simpleURL)
            complete(s"Removed $simpleURL")
          }
      } ~
      path("domains") {
        get {
          complete(urlService.findDomains(new PageRequest(0, 1000)).getContent)
        }
      } ~
      path("domain" / Segment) {
        address =>
          get {
            complete(urlService.findURLs(address, "NOT_VISITED", 0,1000))
          }
      } ~
      //    @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET")
      path("api") {
        get {
          complete {
            val (resourceListing, apiListings) = swaggerApi(List(typeOf[Neo4JHttpService]), List[Type](typeOf[DictEntry])).buildAll
            apiListings
          }
        }
      } ~
      path("stop") {
        complete {
          in(1.second) {
            actorSystem.shutdown()
          }
          "Shutting down in 1 second..."
        }
      }
  }

  def in[U](duration: FiniteDuration)(body: => U): Unit =
    actorSystem.scheduler.scheduleOnce(duration)(body)
}

@ApiModel(description = "an entry in the dictionary")
case class DictEntry(
  @(ApiModelProperty @field)(value = "the key...") key: String,
  value: String,
  expire: Option[Long]
)

