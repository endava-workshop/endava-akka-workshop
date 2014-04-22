package rest.api

import spray.routing.HttpService
import akka.actor.ActorRefFactory
import spray.http.StatusCodes

// TODO incomplete - WIP


//
///**
// * Service that implements the route for SwaggerUI
// */
//class SwaggerUI(implicit val actorRefFactory: ActorRefFactory) extends HttpService {
//
//  def routes = path("swagger") {
//    pathEnd { redirect("/swagger/", StatusCodes.PermanentRedirect) } } ~
//    pathPrefix("swagger") {
//      pathSingleSlash { getFromResource("swagger/index.html") } ~
//        getFromResourceDirectory("swagger")
//    }
//
//}
