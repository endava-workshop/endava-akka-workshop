package akka.ws.pass.rest

import scala.concurrent.Await

import akka.actor.ActorSystem
import spray.json.DefaultJsonProtocol
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer

case class PasswordList_(passwordList: List[String])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val simpleUrlFormat = jsonFormat1(PasswordList_)
}

object RestClient {

  def getPasswords(system: ActorSystem, pageIndex: Int, pageSize: Int): java.util.List[String] = {
    implicit val s = system
    import system.dispatcher
    import MyJsonProtocol._
    import spray.client.pipelining._
    import scala.concurrent.duration._
	import spray.httpx.SprayJsonSupport._
	
    val client = sendReceive ~> unmarshal[Array[String]]
    val response = client(Get("http://localhost:8080/getPasswords/" + pageIndex + "/" + pageSize))
    val result = Await.result(response, 10 seconds)
    val size = result.length;
    //println(s"received a number of $size of passwords")
    
    val javaList = new java.util.ArrayList [String] (result.length)
    result.toList.foreach (javaList.add (_))   

    javaList
  }

}

  
  


