package sample.cluster.simple

import akka.actor.{Actor, ActorLogging, DeadLetter}
import akka.cluster.ClusterEvent.CurrentClusterState
import akka.cluster.ClusterMessage

/**
  * Created by henryjao on 7/2/16.
  */
class EchoActor extends Actor with ActorLogging {

  def receive = {

    //case c: CurrentClusterState if c.members.isEmpty =>

    case DeadLetter(c:ClusterMessage, _ , _) =>
        log.info(s"Cluster DeadLetter: \n$c\n")

    case unknown =>
      log.warning(s"---${sender()} unexpected: $unknown")
  }

}
