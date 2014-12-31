//package dev.example.eventsourcing.web
//
//import java.util.{List => JList}
//import javax.annotation.Resource
//import javax.ws.rs._
//import javax.ws.rs.core.MediaType._
//import javax.xml.bind.annotation._
//
//import com.sun.jersey.api.view.Viewable
//
//import org.springframework.stereotype.Component
//
//import dev.example.eventsourcing.service.InvoiceStatistics
////import dev.example.eventsourcing.util.Binding._
//
//@Component
//@Path("/statistics")
//class StatisticsResource {
//  @Resource
//  var invoiceStatistics: InvoiceStatistics = _
//
//  @GET
//  @Produces(Array(TEXT_XML, APPLICATION_XML, APPLICATION_JSON))
//  def statisticsXmlJson =
//    sc200(Statistics(InvoiceUpdate(invoiceStatistics)))
//
//  @GET
//  @Produces(Array(TEXT_HTML))
//  def statisticsHtml =
//    sc200(new Viewable(webPath("Statistics"), Statistics(InvoiceUpdate(invoiceStatistics))))
//}
//
//@XmlRootElement(name = "statistics")
//case class Statistics(invoiceUpdates: List[InvoiceUpdate]) {
//  private def this() = this(null)
//
//  def invoiceUpdatesSorted =
//    invoiceUpdates.sortWith { (a1, a2) => a1.invoiceId < a2.invoiceId }
//
//  @XmlElementRef
//  @XmlElementWrapper(name = "invoice-updates")
//  def getInvoiceUpdates: JList[InvoiceUpdate] = {
//    import scala.collection.JavaConverters._
//    invoiceUpdates.asJava
//  }
//}
//
//@XmlRootElement(name = "invoice-update")
//@XmlAccessorType(XmlAccessType.FIELD)
//case class InvoiceUpdate(
//  @xmlAttribute(name = "invoice-id") invoiceId: String,
//  @xmlElement(name = "update-count") updateCount: Int) {
//  private def this() = this(null, 0)
//}
//
//object InvoiceUpdate {
//  def apply(invoiceStatistics: InvoiceStatistics): List[InvoiceUpdate] =
//    invoiceStatistics.currentState.map(kv => InvoiceUpdate(kv._1, kv._2)).toList
//}