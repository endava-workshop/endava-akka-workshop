package samples

import org.junit.Assert.assertTrue
import org.junit.Test

import com.endava.command.dto.Domain

import spray.http.DateTime
import spray.json._
import spray.json.DefaultJsonProtocol._

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(Domain)
}

@Test
class AppTest {

  @Test
  def testOK() = assertTrue(true)

  @Test
  def testDomainAsJson() = {
    import MyJsonProtocol._

    val domain: Domain = new Domain("www.wikipedia.org", 500, DateTime.now.toString(), "FOUND")

    val json = domain.toJson

    println(json)
    assertTrue(true)
  }
}


