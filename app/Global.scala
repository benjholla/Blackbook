import controllers._
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import util.ExtendedHeaders._

object Global extends GlobalSettings { 
  
  // When an exception occurs in your application, the onError operation will be called.
  override def onError(request: RequestHeader, ex: Throwable) = {
    controllers.Application.error(request)
  }  
  
  // If the framework doesn’t find an Action for a request, the onHandlerNotFound operation will be called
  override def onHandlerNotFound(request: RequestHeader) = { 
    controllers.Application.notFound(request)
  }
  
  // The onBadRequest operation will be called if a route was found, but it was not possible to bind the request parameters
  override def onBadRequest(request: RequestHeader, error: String) = {
    controllers.Application.notFound(request)
  }  
  
  // You can override the onStart and onStop methods to be notified of the events in the application life-cycle
  override def onStart(app: Application) {
    Logger.info("Application has started")
  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  } 
  
}

