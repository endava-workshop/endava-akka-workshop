import akka.actor.{Actor, Props, ActorSystem}
import akka.io.IO
import akka.japi.Creator
import akka.routing.RoundRobinRouter
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import rest.{DomainURLService, Neo4JHttpService}
import spray.can.Http

// https://github.com/spray/spray/blob/master/examples/spray-can/simple-http-server/src/main/scala/spray/examples/FileUploadHandler.scala
object Main extends App {

  val port = 8080

  implicit val system = ActorSystem()
  implicit val _springContext = new ClassPathXmlApplicationContext("/spring-appContext.xml")
  val handler = {
      val springAwareActorCreator: () ⇒ Actor = () ⇒ new DomainURLService() {
        val springContext: ApplicationContext = _springContext
      }
      system.actorOf(Props(springAwareActorCreator).withRouter(new RoundRobinRouter(5)), name = "handler")
  }
  println( s"Binding to [$port]..." )
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)

}