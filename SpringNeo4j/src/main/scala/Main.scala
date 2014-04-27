import akka.actor.{Actor, Props, ActorSystem}
import akka.io.IO
import akka.routing.RoundRobinRouter
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import rest.DomainURLService
import spray.can.Http

// https://github.com/spray/spray/blob/master/examples/spray-can/simple-http-server/src/main/scala/spray/examples/FileUploadHandler.scala
object Main extends App {

  val port = 8080

  implicit val system = ActorSystem()
  implicit val _springContext = new ClassPathXmlApplicationContext("/spring-appContext.xml")
  val handler = {
      val springAwareActorCreator: () ⇒ Actor = () ⇒ new DomainURLService() {
        val springContext: ApplicationContext = _springContext
        // urlService // INFO: urlService will control storage type: Mongo vs Neo4J
      }
      system.actorOf(Props(springAwareActorCreator).withRouter(new RoundRobinRouter(50)), name = "handler")
  }
  println( s"Binding to [$port]..." )
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = port)

}