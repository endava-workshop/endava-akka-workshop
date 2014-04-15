package rest
import scala.collection.JavaConversions._
import spray.routing._
import scala.concurrent.duration.Duration
import java.util
import com.gettyimages.spray.swagger.SwaggerApiBuilder
import scala.reflect.runtime.universe._
import com.wordnik.swagger.annotations.{ ApiOperation, ApiModel, Api }
import spray.httpx.Json4sSupport
import org.json4s.{ DefaultFormats, Formats }
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import spray.httpx.marshalling._
import scala.concurrent.duration._
import akka.util.Timeout
import spray.util._
import spray.http._
import akka.actor.ActorRef
import ro.endava.akka.workshop.messages.SearchPasswordMessage

@Api(value = "/", description = "This is a ES endpoint.")
abstract class ESHttpService(indexDispatcherActor : ActorRef) extends HttpServiceActor with Json4sSupport {

  implicit def json4sFormats: Formats = DefaultFormats

  implicit val timeout = Timeout(30 seconds)
  //  val urlService = () => springContext.getBean(classOf[UrlService])
  val swaggerApi = new SwaggerApiBuilder("1.2", "1.0", "swagger-specs", _: Seq[Type], _: Seq[Type])

  
  
  @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET") // TODO this needs to be moved at method level
  def receive = runRoute {
    path("getPasswords" / Segment / Segment) {
      (pageIndex, pageSize) =>
        complete {
          // get akka actor
          indexDispatcherActor.tell(new SearchPasswordMessage(indexDispatcherActor, pageIndex.toLong, pageSize.toLong), indexDispatcherActor)
          s"get passwords for index $pageIndex | pageSize $pageSize"
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

