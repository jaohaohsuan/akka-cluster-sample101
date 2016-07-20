package sample.cluster.simple

import java.net.InetAddress

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Address, DeadLetter, PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
//import io.fabric8.kubernetes.client.DefaultKubernetesClient

import scala.concurrent.duration._
import collection.JavaConversions._
import scala.concurrent.Await

object SimpleClusterApp {

  def main(args: Array[String]): Unit = {

    args.foreach { port =>

      //java.net.InetAddress.getAllByName(value).map(_.getHostAddress).toSeq

      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())


      // Create an akka system
      val system = ActorSystem("lego", config)

      //system.actorOf(Props[SimpleClusterListener])
      system.actorOf(Props[ClusterDiscovery]) ! JoinCluster

      //implicit val executionContext = system.dispatcher

      //Thread.sleep(15000)

      system.actorOf(
        ClusterSingletonManager.props(
          singletonProps = Props(classOf[ExamplePersistentActor]),
          terminationMessage = PoisonPill,
          settings = ClusterSingletonManagerSettings(system))
        ,name = s"aggExample")

    }

  }

}
