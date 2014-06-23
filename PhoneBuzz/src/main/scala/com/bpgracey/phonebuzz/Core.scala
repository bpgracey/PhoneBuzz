package com.bpgracey.phonebuzz

import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import spray.can.Http
import com.typesafe.scalalogging.slf4j.LazyLogging
import Config._
import akka.actor.actorRef2Scala

/*
 *  entry point for REPL
 */
object Cli extends App with BootedCore with CoreActors

/*
 *  entry point for HTTP server
 */
object Rest extends App with BootedCore with CoreActors with Api with Web

trait Core {
	implicit def system: ActorSystem
}

/*
 * Actor instantiation
 */
trait CoreActors {
	this: Core =>
		
}

/*
 * Actor system instantiation
 */
trait BootedCore extends Core with LazyLogging {
	logger.info("Actor system {}", actorsystem)
	implicit lazy val system = ActorSystem(actorsystem)
	sys.addShutdownHook(system.shutdown)
}

/*
 * REST API
 */
trait Api {
	this: CoreActors with Core =>
		
	private implicit val _ = system.dispatcher

	val service = system.actorOf(Props(new MainService(MainService.route)))
}

/*
 * HTTP Server
 */
trait Web extends LazyLogging {
	this: Api with CoreActors with Core =>

	logger.info(s"Binding to $bind:$port")
	IO(Http)(system) ! Http.Bind(service, bind, port = port)
}