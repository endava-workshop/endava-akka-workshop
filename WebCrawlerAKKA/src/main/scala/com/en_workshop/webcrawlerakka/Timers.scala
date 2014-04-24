package com.en_workshop.webcrawlerakka

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
 * Created by ionut on 20.04.2014.
 */
trait Timers {

  def timed[T](message: String, errorPrefix: String = "")(f: Future[T])(implicit executionContext: ExecutionContext): Future[T] = {
    val t0 = System.currentTimeMillis()
    f.onComplete {
      case Success(simpleURL) =>
        val t1 = System.currentTimeMillis()
        println(s"${t1 - t0}ms for $message")
      case Failure(t) =>
        val t1 = System.currentTimeMillis()
        println(errorPrefix +  s" -> An error has occurred during [$message] after ${t1 - t0}ms: " + t.getMessage)
    }
    f
  }

}
