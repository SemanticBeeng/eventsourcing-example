package dev.example.eventsourcing.server

import org.eclipse.jetty.server.{Server => JettyServer}
//import org.fusesource.scalate.servlet.TemplateEngineFilter


//object Webserver extends App {
//  val server = new JettyServer(8080);
//  val context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
//
  //val jerseyHolder = new FilterHolder(new SpringServlet)
  //val scalateHolder = new FilterHolder(new TemplateEngineFilter)

//  jerseyHolder.setInitParameter("com.sun.jersey.config.property.packages", "org.fusesource.scalate.console;dev.example.eventsourcing.server")
//  jerseyHolder.setInitParameter("com.sun.jersey.config.feature.Trace", "true")
//  jerseyHolder.setInitParameter("com.sun.jersey.config.feature.Redirect", "true")
//  jerseyHolder.setInitParameter("com.sun.jersey.config.feature.Formatted", "true")
//  jerseyHolder.setInitParameter("com.sun.jersey.config.feature.ImplicitViewables", "true")
//
//  context.setContextPath("/")
//  context.setBaseResource(new FileResource(new URL("file:src/main/webapp")))
//  context.setInitParameter("contextConfigLocation", "/WEB-INF/context.xml")
//  context.addEventListener(new ContextLoaderListener)
//
//  context.addFilter(jerseyHolder, "/*", EnumSet.noneOf(classOf[DispatcherType]))
//  //context.addFilter(scalateHolder, "/*", EnumSet.noneOf(classOf[DispatcherType]))
//  context.addServlet(new ServletHolder(new HttpServlet {}), "/*")
//
//  server.setHandler(context)
//  server.start()
//  server.join()
//
//  class ApplicationInitializer extends ServletContextListener {
//    def contextInitialized(sce: ServletContextEvent) { Appserver.boot() }
//    def contextDestroyed(sce: ServletContextEvent) {}
//  }
//
//  @Provider
//  class JaxbContextResolver extends ContextResolver[JAXBContext] {
//    val paths = "dev.example.eventsourcing.domain:dev.example.eventsourcing.web"
//    val config = JSONConfiguration.mapped().rootUnwrapping(false).build()
//    val context = new JSONJAXBContext(config, paths)
//    def getContext(clazz : Class[_]) = context
//  }
//}



