package scala
import spray.routing._
import spray.http.Uri.Path
import scala.concurrent.duration.Duration
import spray.http.HttpHeaders.`Content-Type`
import spray.http.MediaType
import neo4j.example.Neo4jService
import akka.actor.Props
import akka.routing.RoundRobinRouter
import akka.messages.{RetrieveLink, Seed}
import akka.util.Timeout
import spray.http.{ HttpData, HttpEntity, HttpResponse }
import spray.httpx.marshalling._
import scala.util.{ Failure, Success }
// magic import

import scala.concurrent.{Await, ExecutionContext, Future}
import spray.httpx.marshalling.Marshaller
import scala.concurrent.ExecutionContext.Implicits.global

import spray.routing.directives.CachingDirectives._
import spray.httpx.encoding._

import scala.concurrent.duration.Duration
import spray.routing.HttpService
import spray.routing.authentication.BasicAuth
import spray.routing.directives.CachingDirectives._
import spray.httpx.encoding._
import spray.http.MediaTypes._

import akka.actors.Neo4jWorker
import  akka.pattern.ask
import scala.concurrent.duration._
//import spray.json.{JsonFormat, DefaultJsonProtocol}
//import spray.httpx.SprayJsonSupport._
import spray.httpx.marshalling._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import akka.actor._
import spray.can.Http
import spray.can.server.Stats
import spray.util._
import spray.http._
import HttpMethods._
import MediaTypes._
import spray.can.Http.RegisterChunkHandler



class DemoHttpService extends HttpServiceActor {

  implicit val timeout = Timeout(30 seconds)

    def backend = actorRefFactory.actorOf(Props[Neo4jWorker].withRouter(RoundRobinRouter(nrOfInstances = 10)))
//  def backend = actorRefFactory.actorOf(Props[Neo4jWorker])

  def receive = runRoute {
    path("seed" / Rest) { path =>
      get {
        backend ! Seed()
        complete("seeding initiated.")
      }
    } ~
    path("links" / Segment) { path  =>
      get {
          complete {
            (backend ? RetrieveLink("http://localhost:7474/db/data/node/" + path)).mapTo[String]
          }
      }
    } ~
    pathSingleSlash {
      get {
        complete(
          <html>
            <body>
              <h1>Say hello to <i>spray-can</i>!</h1>
              <p>Defined resources:</p>
              <ul>
                <li><a href="/seed/">/seed</a></li>
                <li><a href="/links/">/links</a></li>
                <li><a href="/stop/">/stop</a></li>
              </ul>
            </body>
          </html>
        )
      }
    } ~
    path("stop") {
      complete {
        in(1.second){ actorSystem shutdown }
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

}
