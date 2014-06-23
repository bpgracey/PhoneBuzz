package com.bpgracey.phonebuzz

import com.typesafe.scalalogging.slf4j.LazyLogging
import akka.actor.{ActorSystem, Props, actorRef2Scala}
import akka.io.IO
import spray.can.Http
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import Config._

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
trait BootedCore extends Core with CoreSSLConfiguration with LazyLogging {
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

/*
 * SSL support
 */
trait CoreSSLConfiguration extends LazyLogging {
	implicit def sslContext: SSLContext = {
		val keyStoreResource = keystore
		val passchar = password toCharArray
		
		val keyStoreType = "jks"
		val factoryType = "SunX509"
		val sslContextType = "TLS"
		
		logger.info("Opening keystore {}", keyStoreResource)
		val keyStore = KeyStore.getInstance(keyStoreType)
		keyStore.load(getClass.getResourceAsStream(keyStoreResource), passchar)
		val keyManagerFactory = KeyManagerFactory.getInstance(factoryType)
		keyManagerFactory.init(keyStore, passchar)
		val trustManagerFactory = TrustManagerFactory.getInstance(factoryType)
		trustManagerFactory.init(keyStore)
		val context = SSLContext.getInstance(sslContextType)
		context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom)
		logger.info("Context opened")
		context
	}
}