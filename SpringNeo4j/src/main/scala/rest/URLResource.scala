package rest

import spray.http._
import MediaTypes._
import com.wordnik.swagger.annotations._
import scala.concurrent.Future
import akka.actor.ActorRefFactory
import spray.routing.{HttpService, Route}
import entity.SimpleURL
import rest.api.{ResourceBase, SimpleUrlData}

/**
 * Defines the /api/url path of our API.
 */
@Api(value = "/url", description = "Allows you to manage URLs")
class URLResource(implicit val actorRefFactory: ActorRefFactory) extends ResourceBase {

  // Absolutely necessary in order to support marshalling.
  implicit val executionContext = actorRefFactory.dispatcher

//  val pipeline = sendReceive ~> unmarshal[SimpleURL]

  // All the routes in this class composed.
  val declaredRoutes = Seq()//Seq(scrapeRoute)

  @ApiOperation(
    value = "Scrape a URL ",
    notes = " Sends a request to http://metascraper.beachape.com to get it scraped.",
    httpMethod = "GET",
    response = classOf[SimpleUrlData]
  )
  @ApiImplicitParams(Array(
    new ApiImplicitParam(
      name = "url",
      value = "The URL to scrape. This should be a JSON string, e.g. { \"url\" : \"http://www.beachape.com/\" } ",
      required = true,
      dataType = "UrlScrape",
      paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Server error ")
  ))
  def domainURLsRoute = path ("domain" / Segment){ address =>
    get {
      respondWithMediaType(`application/json`) {
//        complete(urlService.findURLs(address))
        complete(???)
      }
    }
  }

}
