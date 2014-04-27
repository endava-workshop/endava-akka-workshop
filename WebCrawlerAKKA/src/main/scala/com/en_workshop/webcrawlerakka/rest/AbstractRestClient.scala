package com.en_workshop.webcrawlerakka.rest

import spray.httpx.Json4sSupport
import org.json4s.{DefaultFormats, Formats}
import akka.actor.ActorSystem
import com.en_workshop.webcrawlerakka.Timers

/**
 * Created by ionut on 20.04.2014.
 */
abstract class AbstractRestClient extends Json4sSupport with Timers {
  implicit def json4sFormats: Formats = DefaultFormats
  lazy implicit val system = ActorSystem("spray") // TODO there should be only one ActorSystem...
  implicit val executionContext = system.dispatchers.lookup("rest-client-dispatcher")
  val webRoot = "http://localhost:8080"
}
