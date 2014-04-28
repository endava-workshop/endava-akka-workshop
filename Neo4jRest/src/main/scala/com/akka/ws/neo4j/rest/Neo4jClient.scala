package com.akka.ws.neo4j.rest

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import spray.client.pipelining.Get
import spray.client.pipelining.WithTransformerConcatenation
import spray.client.pipelining.sendReceive
import spray.client.pipelining.sendReceive$default$3
import spray.client.pipelining.unmarshal
import spray.json.DefaultJsonProtocol

case class PasswordList_(passwordList: List[String])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val simpleUrlFormat = jsonFormat1(PasswordList_)
}

object Neo4jClient {

  val neo4jUrl = "http://localhost:7474/db/data/";
  
  def getNeo4jServiceRoot(system: ActorSystem): String = {
    implicit val akkaSystem = system
    import system.dispatcher
    import MyJsonProtocol._
    import spray.client.pipelining._
    import scala.concurrent.duration._
	import spray.httpx.SprayJsonSupport._
	
    val client = sendReceive ~> unmarshal[String]
    val response = client(Get(neo4jUrl))
    val result = Await.result(response, 1 seconds)
    result
  }
  
  def getAllDomains(system: ActorSystem): java.util.List[String] = {
    implicit val akkaSystem = system
    import system.dispatcher
    import MyJsonProtocol._
    import spray.client.pipelining._
    import scala.concurrent.duration._
	import spray.httpx.SprayJsonSupport._
	
	val client = sendReceive ~> unmarshal[Array[String]]
    val response = client(Get(neo4jUrl))
    val result = Await.result(response, 1 seconds)
    val size = result.length;
    val javaList = new java.util.ArrayList [String] (result.length)
    result.toList.foreach (javaList.add (_))   

    javaList  
    }
  
  
}

  
  


