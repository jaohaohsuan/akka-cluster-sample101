package sample.cluster.simple

import java.net.InetAddress

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Address, DeadLetter, Props}
import akka.cluster.Cluster
import io.fabric8.kubernetes.client.DefaultKubernetesClient

import scala.concurrent.duration._
import collection.JavaConversions._
import scala.concurrent.Await

object SimpleClusterApp {

  def main(args: Array[String]): Unit = {

    args.foreach { port =>

      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())

      // Create an akka system
      val system = ActorSystem("storedq", config)

      //val deadLettersSubscriber = system.actorOf(Props[EchoActor], name = "dead-letters-subscriber")

      //system.eventStream.subscribe(deadLettersSubscriber, classOf[DeadLetter])

      system.actorOf(Props[ClusterDiscoveryActor]) ! JoinCluster


    }

  }

}
