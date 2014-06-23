package com.bpgracey.fizzbuzz

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BuzzerSpec extends Specification {
	import fizzBuzzer._

	"FizzBuzz internals" should {
		"Register fizzes" in {
			!(1 isFizz)
			!(2 isFizz)
			(3 isFizz)
			!(4 isFizz)
			12.isFizz
			!(11 isFizz)
			15 isFizz
		}
		
		"Register buzzes" in {
			!(1 isBuzz)
			!(3 isBuzz)
			(5 isBuzz)
			!(6 isBuzz)
			15 isBuzz
		}
		
		"Register fizzbuzzes" in {
			!(1 isFizzBuzz)
			!(2 isFizzBuzz)
			!(3 isFizzBuzz)
			!(5 isFizzBuzz)
			15 isFizzBuzz
		}
	}
	
	"FizzBuzz api" should {
		"Report Non-FizzBuzzes" in {
			1.fizzBuzz === "1"
			2.fizzBuzz === "2"
			14.fizzBuzz == "14"
		}
		
		"Report Fizzes" in {
			3.fizzBuzz must contain("Fizz")
			9.fizzBuzz must contain("Fizz")
			15.fizzBuzz must contain("Fizz")
		}
		
		"Report Buzzes" in {
			5.fizzBuzz must contain("Buzz")
			10.fizzBuzz must contain("Buzz")
			15.fizzBuzz must contain("Buzz")
		}
		
		"Return correct answers" in {
			1.fizzBuzz mustEqual "1"
			3.fizzBuzz mustEqual "Fizz"
			5.fizzBuzz mustEqual "Buzz"
			15.fizzBuzz mustEqual "Fizz Buzz"
		}
	}
}