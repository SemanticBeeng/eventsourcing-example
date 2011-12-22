package dev.example.eventsourcing.event

import akka.dispatch._

trait Event

case class EventLogEntry(logId: Long, logEntryId: Long, seqnr: Long, event: Event)

trait EventLog extends Iterable[EventLogEntry] {
  def iterator: Iterator[EventLogEntry]
  def iterator(fromLogId: Long, fromLogEntryId: Long): Iterator[EventLogEntry]

  def appendAsync(event: Event): Future[EventLogEntry]
  def append(event: Event): EventLogEntry = appendAsync(event).get
}

trait EventLogEntryPublication extends EventLog {
  def channel: Channel[EventLogEntry]

  abstract override def appendAsync(event: Event): Future[EventLogEntry] = {
    val future = super.appendAsync(event)
    future.onResult { case entry => channel.publish(entry) }
    future
  }
}
