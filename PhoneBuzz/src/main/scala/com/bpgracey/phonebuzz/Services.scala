package com.bpgracey.phonebuzz

import com.typesafe.scalalogging.slf4j.LazyLogging
import akka.actor.ActorLogging
import spray.http.StatusCodes._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directives
import spray.routing.HttpServiceActor
import spray.routing.Route
import spray.routing.RejectionHandler
import spray.routing.MissingQueryParamRejection
import spray.routing.MalformedQueryParamRejection
import com.bpgracey.fizzbuzz._
import Config._
import spray.http.MediaTypes

/**
 * @author Bancroft Gracey
 *
 */
class MainService(route: Route) extends HttpServiceActor with ActorLogging {
	def receive: Receive = runRoute(route)
}

object MainService extends PhoneRoute with LazyLogging {
	def route = 
		pathPrefix("/phonebuzz") {
			phoneRoute
		} ~
		complete(NotFound)
}

case class PhoneReq(number: Int)

trait PhoneRoute extends Directives with LazyLogging {
	import Answer._
	val urlHandler = RejectionHandler {
		case MissingQueryParamRejection(p) :: _ =>
			complete(NotAcceptable, s"Missing parameter '${p}'")
		case _ =>
			complete(NotFound, "Path error")
	}
	val phoneRoute =
		handleRejections(urlHandler) {
			respondWithMediaType(MediaTypes.`application/xml`) {
				path("call") {
					(post | get) {
						complete(firstAnswer)
					}
				} ~
				path("number") {
					(post | get) {
						anyParam('Digits.as[Int]).as(PhoneReq) { req =>
							complete {
								import fizzBuzzer._
								req match {
									case PhoneReq(n) if n < 1 => noNumber
									case PhoneReq(n) if n > max => badNumber(n)
									case PhoneReq(n) => resultAnswer(n, n fizzBuzzTo)
								}
							}
						}
					}
				}
			}
	}
}