package sample.cluster.simple

import com.typesafe.config.ConfigFactory
import akka.actor.{ActorSystem, Address, DeadLetter, Props}
import akka.cluster.Cluster
import io.fabric8.kubernetes.client.DefaultKubernetesClient
import collection.JavaConversions._

object SimpleClusterApp {

  def main(args: Array[String]): Unit = {
      startup(args)
  }

  def startup(ports: Seq[String]): Unit = {

    val kube = new DefaultKubernetesClient()
    val pods = kube.pods().list()
    for {
      po <- pods.getItems.toList
    } yield {
      println(s"${po.getStatus.getPodIP}")
    }

    ports foreach { port =>
      // Override the configuration of the port
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
        withFallback(ConfigFactory.load())



      // Create an Akka system
      val system = ActorSystem("ClusterSystem", config)

      val cluster = Cluster(system)

      system.actorOf(Props[SimpleClusterListener])

      if (port == "2552") {
        cluster.join(cluster.selfAddress)
      }
      else
        cluster.joinSeedNodes(Address("akka.tcp", "ClusterSystem", Some("127.0.0.1"), Some(2552)):: Nil)



//      val deadLettersSubscriber = system.actorOf(Props[EchoActor], name = "dead-letters-subscriber")
//      val echoActor = system.actorOf(Props[EchoActor], name = "generic-echo-actor")
//
//      system.eventStream.subscribe(deadLettersSubscriber, classOf[DeadLetter])
//
//      import system.dispatcher
//      import scala.concurrent.duration._
//
//      system.scheduler.scheduleOnce(5 seconds) {
//        echoActor ! cluster.state
//      }


      // Create an actor that handles cluster domain events
      //system.actorOf(Props[SimpleClusterListener], name = "clusterListener")


    }
  }

}
