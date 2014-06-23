package com.bpgracey.phonebuzz

object Answer {
	import Config._
	
	def badNumber(number: Int) = {
<Response>
    <Say>I'm sorry, I heard {number}, which is more than {max}. Goodbye!</Say>
</Response>
		
	}
	
	val noNumber = {
<Response>
    <Say>I'm sorry, I heard zero, which is less than 1. Goodbye!</Say>
</Response>
		
	}

	def resultAnswer(number: Int, results: Seq[String]) = {
		val nodes = results map {r => <Say>{r}.</Say>}
		
<Response>
	<Say>I heard {number}. The Fizz Buzz list from 1 to {number} is:</Say>
	{nodes}
	<Say>Goodbye!</Say>
</Response>
	}
	
	val firstAnswer =
<Response>
    <Gather action="/phonebuzz/number" method="GET" timeout="10" finishOnKey="#">
        <Say>
            Please enter a number between 1 and {max},
            followed by the pound sign
        </Say>
    </Gather>
    <Say>We didn't receive any input. Goodbye!</Say>
</Response>
	
}

