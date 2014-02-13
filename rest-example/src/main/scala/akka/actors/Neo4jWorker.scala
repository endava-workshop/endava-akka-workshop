package akka.actors

import akka.actor.Actor
import neo4j.example.Neo4jService
import akka.event.Logging
import akka.messages.{RetrieveLink, Seed}
import java.net.URI
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{ Failure, Success }
//http://nurkiewicz.blogspot.ro/2013/03/futures-in-akka-with-scala.html

class Neo4jWorker() extends Actor {

  val log = Logging(context.system, this)

  implicit val xc: ExecutionContext = ExecutionContext.global

  val service: Neo4jService = new Neo4jService()

  def receive = {
    case Seed() =>
      Future {
        service.populateDatabase()
      }.onComplete( uri =>
        println("database populated: " + uri)
      )
    case RetrieveLink(link) =>
      Future {
        service.findLinksForNode(new URI(link))
      } onComplete {
        case Success(links) => links
        case Failure(ex) => ex.getMessage
      }
    case x =>
      println("What do you mean? " + x)

  }

}
