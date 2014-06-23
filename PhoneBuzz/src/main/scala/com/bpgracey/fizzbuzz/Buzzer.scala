package com.bpgracey.fizzbuzz

import com.bpgracey.phonebuzz.Config

object fizzBuzzer {
	import Config._
	implicit class FizzBuzz(number: Int) {
		lazy val isFizz = number % 3 == 0
		lazy val isBuzz = number % 5 == 0
		lazy val isFizzBuzz = isFizz && isBuzz
		lazy val fizzBuzz: String = {
			if (isFizzBuzz) "Fizz Buzz"
			else if (isFizz) "Fizz"
			else if (isBuzz) "Buzz"
			else number toString
		}
		def fizzBuzzTo(): Seq[String] = {
			require(number > 0)
			require(number <= max)
			(1 to number) map {_ fizzBuzz}
		}
	}
	
}