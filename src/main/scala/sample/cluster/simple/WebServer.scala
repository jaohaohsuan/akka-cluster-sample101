package sample.cluster.simple

import akka.actor.{ActorSystem, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging

import akka.pattern.ask

/**
  * Created by henryjao on 7/18/16.
  */
object WebServer extends LazyLogging {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("lego")

    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val persistentProxy = system.actorOf(
      ClusterSingletonProxy.props(
        singletonManagerPath = "/user/aggExample",
        settings = ClusterSingletonProxySettings(system)),
      name = "persistentProxy")

//    val route =
//      path("hello") {
//        get {
//          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
//        }
//      }

    val handler = get {
      complete("Hello world!")
    } ~ post {
      path(Segment) { p =>
        persistentProxy ! Cmd(p)
        complete(OK, s"$p is appended")
      }
    }

    val bindingFuture = Http().bindAndHandle(handler, "0.0.0.0", 7879)

    bindingFuture.onFailure {
      case ex: Exception =>
        logger.warn("host: 0.0.0.0, port: 7879 cannot be bind", ex)
    }

    system.actorOf(Props[ClusterDiscovery]) ! JoinCluster
  }
}
