package rest

import java.util
import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.reflect.runtime.universe._
import org.json4s.DefaultFormats
import org.json4s.Formats
import com.gettyimages.spray.swagger.SwaggerApiBuilder
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiOperation
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import ro.endava.akka.workshop.actors.IndexDispatcherActor
import ro.endava.akka.workshop.actors.SearchRouterActor
import ro.endava.akka.workshop.messages.SearchPasswordMessage
import ro.endava.akka.workshop.messages.SearchPasswordResultMessage
import spray.httpx.Json4sSupport
import spray.routing._
import spray.util._
import scala.concurrent.Await
import ro.endava.akka.workshop.dto.Page
import ro.endava.akka.workshop.messages.IndexMessage

@Api(value = "/", description = "This is a ES endpoint.")
class ESHttpService(akkaSystem : ActorSystem) extends HttpServiceActor with Json4sSupport {

  //searchRouterActor: ActorRef, indexActor:ActorRef
  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(5 seconds)
  //  val urlService = () => springContext.getBean(classOf[UrlService])
  val swaggerApi = new SwaggerApiBuilder("1.2", "1.0", "swagger-specs", _: Seq[Type], _: Seq[Type])

  val searchRouterActor = akkaSystem.actorOf(Props[SearchRouterActor])
  val indexActor = akkaSystem.actorOf(Props[IndexDispatcherActor])
  
  @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET") // TODO this needs to be moved at method level
  def receive = runRoute {
    path("getPasswords" / Segment / Segment) {
      (pageIndex, pageSize) =>
        get {
          // get akka actor
          val future: Future[SearchPasswordResultMessage] = ask(searchRouterActor, new SearchPasswordMessage(pageIndex.toLong, pageSize.toLong)).mapTo[SearchPasswordResultMessage]
          val response = Await.result(future, 5 seconds)
          println("response " + response.getPasswords().size())
          complete(response.getPasswords())
        }
    } ~
      path("indexPage") {
          put {
            entity(as[Page]) { page =>
              indexActor.tell(new IndexMessage(page.getUrl(), page.getContent()), sender)
            complete(s"page sent for being indexed")
            }
          }
      } ~
      //    @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET")
      path("api") {
        get {
          complete {
            val (resourceListing, apiListings) = swaggerApi(List(typeOf[ESHttpService]), List[Type](typeOf[DictEntry])).buildAll
            apiListings
          }
        }
      }
  }

  def in[U](duration: FiniteDuration)(body: => U): Unit =
    actorSystem.scheduler.scheduleOnce(duration)(body)
}

@ApiModel(description = "an entry in the dictionary")
case class DictEntry(
  val key: String,
  val value: String,
  val expire: Option[Long])

