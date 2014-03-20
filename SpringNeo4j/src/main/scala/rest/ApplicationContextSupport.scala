package rest

import org.springframework.context.ApplicationContext

/**
 * Created by ionut on 3/20/14.
 */
trait ApplicationContextSupport {
  implicit val springContext: ApplicationContext
}
