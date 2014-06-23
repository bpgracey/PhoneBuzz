package com.bpgracey.phonebuzz

import com.typesafe.scalalogging.slf4j.LazyLogging
import akka.actor.ActorLogging
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.{Directives, HttpServiceActor, Route, RejectionHandler, MissingQueryParamRejection}
import com.twilio.sdk.{TwilioRestClient}
import collection.JavaConverters._
import spray.http.StatusCodes._
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

object MainService extends PhoneRoute with CallRoute with LazyLogging {
	val route = 
		pathPrefix("phonebuzz") {
			callRoute ~
			phoneRoute
		} ~
		complete(NotFound)
}

trait CallRoute extends Directives with LazyLogging {
	lazy val client = new TwilioRestClient(twilioSid, twilioToken)
	lazy val callFactory = client.getAccount().getCallFactory()
	val callRoute =
		pathEndOrSingleSlash {
			(get | post) {
				complete {
<html>
  <head>
    <title>PhoneBuzz - Fizzbuzz by Phone!</title>
  </head>
  <body>
    <h1>PhoneBuzz!</h1>
    <form method="get" action="/phonebuzz/makecall">
      Phone number to call you at:
      <input type="text" name="phone" />
      <button type="submit">Call me!</button>
    </form>
  </body>
</html>
				}
			}
	} ~
	path("makecall") {
		(get | post) {
			anyParam('phone) { phone =>
				complete {
					val callParams = Map[String, String](
							"To" -> phone, 
							"From" -> twilioPhone, 
							"Url" -> "/phonebuzz/call").asJava
					val call = callFactory.create(callParams)
					val callSid = call.getSid()
<html>
  <head>
    <title>PhoneBuzz - Fizzbuzz by Phone!</title>
  </head>
  <body>
    <h1>PhoneBuzz!</h1>
    <form method="get" action="/phonebuzz/makecall">
      Phone number to call you at:
      <input type="text" name="phone" value="{phone}" />
      <button type="submit">Call me!</button>
    </form>
    <p>Your call has been placed!<br/>Your SID is {callSid}</p>
  </body>
</html>
				}
			}
		}
	}
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