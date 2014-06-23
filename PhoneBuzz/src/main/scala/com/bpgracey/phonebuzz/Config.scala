package com.bpgracey.phonebuzz

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging

object Config extends LazyLogging {
  lazy val config = ConfigFactory.load().getConfig("phonebuzz")
  
  def getString(k: String) = {
  	val v = config.getString(k)
  	logger.debug(s"$k = $v")
  	v
  }
  lazy val bind = getString("bind.ip")
  lazy val actorsystem = getString("actor.system")
  
  def getInt(k: String) = {
  	val v = config.getInt(k)
  	logger.debug(s"$k = $v (Int)")
  	v
  }
  lazy val port = getInt("bind.port")
  lazy val max = getInt("fizzbuzz.max")
}