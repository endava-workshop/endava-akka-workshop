package rest.api

import spray.routing.{HttpService, Route}

/**
 * Trait for ApiService classes to inherit from
 * @see https://github.com/lloydmeta/spray-servlet-scratchpad
 */
trait ResourceBase extends HttpService {

  val pathPrefix = "api"
  def declaredRoutes: Seq[Route]

  def routes: Route = pathPrefix(pathPrefix) {
    declaredRoutes.reduceLeft(_ ~ _)
  }
}
