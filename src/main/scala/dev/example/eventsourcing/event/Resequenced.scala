package dev.example.eventsourcing.event

import akka.actor.{Props, ActorSystem, Actor, ActorRef}

trait Resequenced extends ChannelSubscriber[EventLogEntry] {
  implicit val system = ActorSystem("eventsourcing-example")

  private val registry = system.actorOf(Props(new ResequencerRegistry(this)))//.start

  abstract override def receive(message: EventLogEntry) =
    registry ! message

  private[event] def dispatch(message: EventLogEntry) =
    super.receive(message)
}

private[event] class ResequencerRegistry(target: Resequenced) extends Actor {
  implicit val system = ActorSystem("eventsourcing-example")
  var resequencers = Map.empty[Long, ActorRef] // logId -> resequencer mapping

  def receive = {
    case entry: EventLogEntry => resequencer(entry.logId) forward entry
  }

  def resequencer(logId: Long) = resequencers.get(logId) match {
    case Some(resequencer) => resequencer
    case None              => {
      resequencers = resequencers + (logId -> system.actorOf(Props(new Resequencer(target))))//.start)
      resequencers(logId)
    }
  }
}

private[event] class Resequencer(target: Resequenced) extends Actor {
  import scala.collection.mutable.Map

  val delayed = Map.empty[Long, EventLogEntry]
  var delivered = -1L

  def receive = {
    case entry: EventLogEntry => resequence(entry)
  }

  private def resequence(entry: EventLogEntry) {
    if (entry.seqnr == delivered + 1) {
      delivered = entry.seqnr
      target.dispatch(entry)
    } else {
      delayed += (entry.seqnr -> entry)
    }
    delayed.remove(delivered + 1).foreach(resequence)
  }
}

