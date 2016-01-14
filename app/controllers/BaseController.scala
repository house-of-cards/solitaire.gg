package controllers

import models.history.RequestLog
import play.api.i18n.I18nSupport
import services.history.RequestHistoryService
import nl.grons.metrics.scala.FutureMetrics
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import utils.metrics.Instrumented
import utils.{ ApplicationContext, Logging }

import scala.concurrent.Future

abstract class BaseController() extends Controller with I18nSupport with Instrumented with FutureMetrics with Logging {
  def ctx: ApplicationContext

  override def messagesApi = ctx.messagesApi

  def req(action: String)(block: (Request[AnyContent]) => Future[Result]) = Action.async { implicit request =>
    timing(action) {
      val startTime = System.currentTimeMillis
      val response = {
        block(request).map { r =>
          val duration = (System.currentTimeMillis - startTime).toInt
          logRequest(request, duration, r.header.status)
          r
        }
      }
      response
    }
  }

  def withAdminSession(action: String)(block: (Request[AnyContent]) => Future[Result]) = Action.async { implicit request =>
    timing(action) {
      val startTime = System.currentTimeMillis
      val cookie = request.cookies.get("role").map(_.value)
      if (cookie.contains("admin")) {
        block(request).map { r =>
          val duration = (System.currentTimeMillis - startTime).toInt
          logRequest(request, duration, r.header.status)
          r
        }
      } else {
        Future.successful(NotFound("404 Not Found"))
      }
    }
  }

  private[this] def logRequest(request: RequestHeader, duration: Int, status: Int) = {
    val log = RequestLog(request, duration, status)
    RequestHistoryService.insert(log)
  }
}
