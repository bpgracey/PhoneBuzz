package com.bpgracey.phonebuzz

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging

object Config extends LazyLogging {
  lazy val config = ConfigFactory.load().getConfig("phonebuzz")
  
  def getString(k: String) = {
  	val v = config.getString(k)
  	logger.debug("""Read {} = "{}"""", k, v)
  	v
  }
  def getStringP(k: String) = {
  	val v = config.getString(k)
  	logger.debug("Read {} = ******", k)
  	v
  }
  lazy val bind = getString("bind.ip")
  lazy val actorsystem = getString("actor.system")
  lazy val keystore = getString("ssl.keystore")
  lazy val password = getStringP("ssl.password")
  lazy val twilioSid = getStringP("twilio.sid")
  lazy val twilioToken = getStringP("twilio.token")
  lazy val twilioPhone = getString("twilio.phone")
  
  def getInt(k: String) = {
  	val v = config.getInt(k)
  	logger.debug("Read {} = {} (Int)", k, v.toString)
  	v
  }
  lazy val port = getInt("bind.port")
  lazy val max = getInt("fizzbuzz.max")
}