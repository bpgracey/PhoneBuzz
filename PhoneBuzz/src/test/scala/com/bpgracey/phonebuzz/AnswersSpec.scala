package com.bpgracey.phonebuzz

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AnswersSpec extends Specification {
	import Answer._
	sequential
	
	"BadNumber" should {
		"give the right message" in {
			val say = badNumber(5) \ "Say"
			say.text must contain("""I'm sorry, I heard 5, which is more than 45. Goodbye!""")
		}
	}
	
	"ResultAnswer" should {
		"give the right message" in {
			val result = resultAnswer(-9, Seq("A", "B"))
			val says = (result \ "Say") map(_.text)
			says mustEqual Seq("""I heard -9. The Fizz Buzz list from 1 to -9 is:""", """A.""", """B.""", """Goodbye!""")
		}
	}
}