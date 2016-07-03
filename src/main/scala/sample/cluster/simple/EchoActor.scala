package sample.cluster.simple

import akka.actor.{Actor, DeadLetter}
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterMessage

/**
  * Created by henryjao on 7/2/16.
  */
class EchoActor extends Actor {

  def receive = {

    case c: CurrentClusterState if c.members.isEmpty =>
      context.system.terminate()
    case DeadLetter(c:ClusterMessage, _ , _) =>
      println(s"$c------")

    case msg => println(s"New msg received: $msg")
  }

}
