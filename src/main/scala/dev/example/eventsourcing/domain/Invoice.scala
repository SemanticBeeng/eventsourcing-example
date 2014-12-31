package dev.example.eventsourcing.domain

import dev.example.eventsourcing.event.Event

import scala.collection.immutable.Nil


sealed abstract class Invoice extends Aggregate[Invoice] with Handler[InvoiceEvent, Invoice] {
  def items: List[InvoiceItem]

  def discount: BigDecimal

  def total: BigDecimal = sum - discount

  def sum: BigDecimal = items.foldLeft(BigDecimal(0)) {
    (sum, item) => sum + item.amount * item.count
  }

  def getTotal = total

  def getSum = sum
}

object Invoice extends Handler[InvoiceEvent, Invoice] {
  def create(id: String): Update[InvoiceEvent, DraftInvoice] =
    update(InvoiceCreated(id), transitionToDraft)

  def handle: PartialFunction[InvoiceEvent, Invoice] =
    transitionToDraft

  def handle(events: List[InvoiceEvent]): Invoice =
    events.drop(1).foldLeft(handle(events(0))) { (invoice, event) => invoice.handle(event)}

  private def transitionToDraft: PartialFunction[InvoiceEvent, DraftInvoice] = {
    case InvoiceCreated(id) => DraftInvoice(id, 0)
  }
}

case class DraftInvoice(
                         id: String,
                         version: Long = -1,
                         items: List[InvoiceItem] = Nil,
                         discount: BigDecimal = 0)
  extends Invoice {
  private def this() = this(id = null) // needed by JAXB

  def addItem(item: InvoiceItem): Update[InvoiceEvent, DraftInvoice] =
    update(InvoiceItemAdded(id, item), transitionToDraft)

  def setDiscount(discount: BigDecimal): Update[InvoiceEvent, DraftInvoice] =
    if (sum <= 100) Update.reject(DomainError("discount only on orders with sum > 100"))
    else update(InvoiceDiscountSet(id, discount), transitionToDraft)

  def sendTo(address: InvoiceAddress): Update[InvoiceEvent, SentInvoice] =
    if (items.isEmpty) Update.reject(DomainError("cannot send empty invoice"))
    else update(InvoiceSent(id, this, address), transitionToSent)

  def handle: PartialFunction[InvoiceEvent, Invoice] =
    transitionToDraft orElse transitionToSent

  private def transitionToDraft: PartialFunction[InvoiceEvent, DraftInvoice] = {
    case InvoiceItemAdded(_, item) => copy(version = version + 1, items = items :+ item)
    case InvoiceDiscountSet(_, discount_) => copy(version = version + 1, discount = discount_)
  }

  private def transitionToSent: PartialFunction[InvoiceEvent, SentInvoice] = {
    case InvoiceSent(id_, _, address_) => SentInvoice(id_, version + 1, items, discount, address_)
  }
}

case class SentInvoice(
                        id: String,
                        version: Long = -1,
                        items: List[InvoiceItem] = Nil,
                        discount: BigDecimal = 0,
                        address: InvoiceAddress)
  extends Invoice {
  private def this() = this(id = null, address = null) // needed by JAXB

  def pay(amount: BigDecimal): Update[InvoiceEvent, PaidInvoice] =
    if (amount < total) Update.reject(DomainError("paid amount less than total amount"))
    else update(InvoicePaid(id), transitionToPaid)

  def handle: PartialFunction[InvoiceEvent, Invoice] =
    transitionToPaid

  private def transitionToPaid: PartialFunction[InvoiceEvent, PaidInvoice] = {
    case InvoicePaid(invoiceId) => PaidInvoice(invoiceId, version + 1, items, discount, address)
  }
}

case class PaidInvoice(
                        id: String,
                        version: Long = -1,
                        items: List[InvoiceItem] = Nil,
                        discount: BigDecimal = 0,
                        address: InvoiceAddress)
  extends Invoice {
  private def this() = this(id = null, address = null) // needed by JAXB

  def paid = true

  def handle = throw new MatchError
}

case class InvoiceItem(
                        description: String,
                        count: Int,
                        amount: BigDecimal) {
  private def this() = this(null, 0, 0)
}

/**
 * Needed to support conditional updates via XML/JSON Web API,
 */
case class InvoiceItemVersioned(
                                 description: String,
                                 count: Int,
                                 amount: BigDecimal,
                                 invoiceVersion: Long = -1) {
  private def this() = this(null, 0, 0)

  def toInvoiceItem = InvoiceItem(description, count, amount)

  def invoiceVersionOption = if (invoiceVersion == -1L) None else Some(invoiceVersion)
}

case class InvoiceAddress(name: String, street: String, city: String, country: String) {
  private def this() = this(null, null, null, null)
}

sealed trait InvoiceEvent extends Event {
  def invoiceId: String
}

case class InvoiceCreated(invoiceId: String) extends InvoiceEvent

case class InvoiceItemAdded(invoiceId: String, item: InvoiceItem) extends InvoiceEvent

case class InvoiceDiscountSet(invoiceId: String, discount: BigDecimal) extends InvoiceEvent

case class InvoiceSent(invoiceId: String, invoice: Invoice, to: InvoiceAddress) extends InvoiceEvent

case class InvoicePaid(invoiceId: String) extends InvoiceEvent

