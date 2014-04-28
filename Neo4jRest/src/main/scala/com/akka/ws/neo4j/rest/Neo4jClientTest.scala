package com.akka.ws.neo4j.rest

import akka.actor.ActorSystem

object Neo4jClientTest extends App {

  implicit val system = ActorSystem()
import system.dispatcher // execution context for futures

  
  val res = Neo4jClient.getNeo4jServiceRoot(system)
  println(res)

  system.shutdown()

}