package samples

import org.junit.Assert.assertTrue
import org.junit.Test
import com.endava.command.dto.Domain
import com.endava.command.dto.DomainLink
import com.endava.command.dto.DomainLink
import com.endava.command.dto.Link
import spray.http.DateTime
import spray.json._
import spray.json.DefaultJsonProtocol._
import com.endava.command.dto.DomainLink

object DomainLinkProtocol extends DefaultJsonProtocol {
  implicit val domainFormat = jsonFormat4(Domain)
  implicit val linkFormat = jsonFormat3(Link)

  implicit val domainLinkFormat = jsonFormat2(DomainLink)
  
//  implicit object DomainJsonFormat extends RootJsonFormat[Domain] {
//    override def write(domain: Domain): JsValue = domain.toJson
//    override def read(v: JsValue): Domain = throw new IllegalStateException("Not Implemented")
//  }
//  
//  implicit object LinkJsonFormat extends RootJsonFormat[Link] {
//    override def write(link: Link): JsValue = link.toJson
//    override def read(v: JsValue): Link = throw new IllegalStateException("Not Implemented")
//  }
//  implicit val jsonFormat = jsonFormat2(DomainLink)
  
}

@Test
class JsonTest {

  @Test
  def testDomainAsJson() = {
    import DomainLinkProtocol._

    val domain: Domain = Domain("www.wikipedia.org", 500, DateTime.now.toString(), "FOUND")
  	val link = Link("www.wikipedia.org", "www.wikipedia.org/test", "FOUND")
    val domainLink = new DomainLink(domain, link)

    val json = domainLink.toJson

    println(json)
    assertTrue(true)
  }
}


