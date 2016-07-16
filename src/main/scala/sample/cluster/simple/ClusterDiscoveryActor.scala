package sample.cluster.simple


import java.net.InetAddress

import akka.actor.{Actor, ActorLogging, Address}
import akka.cluster.Cluster

import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

case object JoinCluster

case class SeedNodes(seq: Seq[String])

class ClusterDiscoveryActor extends Actor with ActorLogging {

  private lazy val DISCOVERY_SERVICE = Option(System.getenv("DISCOVERY_SERVICE"))
  private lazy val IFAC = Option(System.getenv("IFAC")).getOrElse("eth0")

  val cluster = Cluster(context.system)

  implicit val executionContext = context.dispatcher

  def receive = {

    case JoinCluster             => self ! getSeedNodes
    case Success(SeedNodes(seq)) => join(seq)
    case Failure(ex: java.net.UnknownHostException) =>
      context.system.scheduler.scheduleOnce(3 seconds, self, JoinCluster)
      log.warning("akka-discovery-svc resolve fail.")
  }

  private def getSeedNodes = {
    Try(
      SeedNodes(DISCOVERY_SERVICE match {
      case Some(value) if value.matches("""[\w.]+\.\w+""") => java.net.InetAddress.getAllByName(value).map(_.getHostAddress).toSeq
      case _ => Seq(getHostAddress)
    }))
  }

  private def join(seedNodes: Seq[String]) = {
    cluster.joinSeedNodes(seedNodes.map{ addr => Address("akka.tcp", context.system.name, Some(addr), Some(2600))})
  }

  private def getHostAddress: String = {
    import java.net._
    NetworkInterface.getNetworkInterfaces
      .find(_.getName equals IFAC)
      .flatMap { interface =>
        interface.getInetAddresses.find(_.isSiteLocalAddress).map(_.getHostAddress)
      }
      .getOrElse(InetAddress.getLocalHost.getHostAddress)
  }

}
