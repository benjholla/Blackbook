package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone        
      }
    }
    
    "redirect to products from root" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get
        
        status(home) must equalTo(SEE_OTHER)
        redirectLocation(home) must beSome.which(_.contains("/products"))
      }
    }

    "render the products page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/products")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("product")
      }
    }
  }
}
