package dev.example.eventsourcing.state

import java.util.concurrent.CompletableFuture

import akka.actor._
import akka.dispatch._
import akka.agent.Agent

import scala.concurrent.ExecutionContext.Implicits.global

//import akka.stm._

import dev.example.eventsourcing.domain._
import dev.example.eventsourcing.event._

import scala.concurrent.Future
import scala.concurrent.stm.Ref
import scalaz._

trait Projection[S, A] {
  def initialState: S
  def currentState: S

  def project: PartialFunction[(S, A), S]
}

trait UpdateProjection[S, A] extends Projection[S, A] {
  implicit val system = ActorSystem("eventsourcing-example")

  private lazy val ref = Ref(initialState)
  private lazy val updater = system.actorOf(Props(new Updater))//.start()

  def eventLog: EventLog
  def writeAhead: Boolean = true

  def currentState: S = ref()

  def transacted[B <: A](update: S => Update[Event, B]): Future[DomainValidation[B]] = {
    val promise = new DefaultCompletableFuture[DomainValidation[B]] ///@todo was DefaultCompletableFuture
    def dispatch = updater ! ApplyUpdate(update, promise.asInstanceOf[CompletableFuture[DomainValidation[A]]])

    //@todo port thhis
/*
    if (Stm.activeTransaction) {
      currentState // join
      deferred(dispatch)
    } else {
      dispatch
    }
*/
    promise
  }

  private case class ApplyUpdate(update: S => Update[Event, A], promise: CompletableFuture[DomainValidation[A]])

  private class Updater extends Actor {
    //@todo: port this
    //if (writeAhead) self.dispatcher = Dispatchers.newThreadBasedDispatcher(self)

    def receive = {
      case ApplyUpdate(u, p) => {
        val current = currentState
        val update = u(current)

        update() match {
          case (events, s @ Success(result)) => {
            log(events.reverse) // TODO: handle errors
            ref set project(current, result.asInstanceOf[A])
            p.complete(s)
          }
          case (_, f) => {
            p.complete(f)
          }
        }
      }
    }

    def log(events: List[Event]) = events.foreach { event =>
      if (writeAhead) eventLog.append(event) else eventLog.appendAsync(event)
    }
  }
}

trait EventProjection[S] extends Projection[S, Event] with ChannelSubscriber[EventLogEntry] {

  private lazy val agent = Agent(Snapshot(-1L, -1L, initialState))

  def currentState: S = currentSnapshot.state

  def currentSnapshot = agent()

  def receive(entry: EventLogEntry) = update(entry)

  def handles(event: Event) = project.isDefinedAt((null.asInstanceOf[S], event))

  def update(entry: EventLogEntry) = if (handles(entry.event)) agent send { snapshot =>
    Snapshot(entry.logId, entry.logEntryId, project(snapshot.state, entry.event))
  }

  def recover(eventLog: EventLog) = currentSnapshot match {
    case Snapshot(-1L, _, _) =>
      eventLog.iterator.foreach(update)
    case Snapshot(fromLogId, fromLogEntryId, event) => {
      val iterator = eventLog.iterator(fromLogId, fromLogEntryId)
      iterator.next() // ignore already processed event
      iterator.foreach(update)
    }
  }

  protected def await() = null //@todo port agent.await()
}
