/**
 *
 */
package com.bpgracey.phonebuzz

import spray.testkit.Specs2RouteTest
import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import spray.http.StatusCodes
import spray.http.MediaTypes
import spray.httpx.unmarshalling.BasicUnmarshallers
import BasicUnmarshallers.NodeSeqUnmarshaller
import xml._


@RunWith(classOf[JUnitRunner])
class PhoneSpec extends Specification with Specs2RouteTest with PhoneRoute {
	"/call" should {
		"reply with the default answer" in {
			Get("/call") ~> phoneRoute ~> check {
				status mustEqual StatusCodes.OK
				responseAs[String] must contain("""Please enter a number between 1 and""")
			}
			Post("/call") ~> phoneRoute ~> check {
				status mustEqual StatusCodes.OK
				responseAs[String] must contain("""Please enter a number between 1 and""")				
			}
		}
	}
	
	"/number" should {
		"only work for POST or GET" in {
			Head("/number") ~> phoneRoute ~> check {
				status mustEqual StatusCodes.NotFound
			}
		}
		"only work if a number is present" in {
			Get("/number?Digits=0") ~> phoneRoute ~> check {
				status mustEqual StatusCodes.OK
				responseAs[String] must contain("""I'm sorry, I heard zero, which is less than 1. Goodbye!""")
			}
			Get("/number?Digits=000") ~> phoneRoute ~> check {
				status mustEqual StatusCodes.OK
				responseAs[String] must contain("""I'm sorry, I heard zero, which is less than 1. Goodbye!""")
			}
		}
		"not work if the number is out of range" in {
			Get("/number?Digits=9999") ~> phoneRoute ~> check {
				status mustEqual StatusCodes.OK
				responseAs[String] must contain("""I'm sorry, I heard 9999, which is more than""")
			}			
		}
		"work for a valid number" in {
			Get("/number?Digits=16") ~> phoneRoute ~> check {
				responseAs[NodeSeq] \ "Say" map (_.text) mustEqual Seq("""I heard 16. The Fizz Buzz list from 1 to 16 is:""",
						"1.", "2.", "Fizz.", "4.", "Buzz.",
					"Fizz.", "7.", "8.", "Fizz.", "Buzz.", 
					"11.", "Fizz.", "13.", "14.", "Fizz Buzz.", "16.",
					"Goodbye!")
			}
		}
	}
}

@RunWith(classOf[JUnitRunner])
class MainSpec extends Specification with Specs2RouteTest {
	sequential
	
	"Initial call" should {
		"reply with the default answer" in {
			Get("/phonebuzz/call") ~> MainService.route ~> check {
				status mustEqual StatusCodes.OK
				responseAs[String] must contain("""Please enter a number between 1 and 45""")
			}
		}
	}
	
	"Main call" should {
		"process a correct response" in {
			Get("/phonebuzz/number?Digits=16") ~> MainService.route ~> check {
				status mustEqual StatusCodes.OK
				responseAs[NodeSeq] \ "Say" map (_.text) mustEqual Seq("""I heard 16. The Fizz Buzz list from 1 to 16 is:""",
						"1.", "2.", "Fizz.", "4.", "Buzz.",
					"Fizz.", "7.", "8.", "Fizz.", "Buzz.", 
					"11.", "Fizz.", "13.", "14.", "Fizz Buzz.", "16.",
					"Goodbye!")
			}
		}
	}
}