package me.archmagece

import org.apache.commons.lang3.RandomStringUtils

// fun me.archmagece.generateToken() = RandomStringUtils.randomAscii(3)!!
// fun me.archmagece.generateToken() = RandomStringUtils.random(3)!!
fun generateToken() = RandomStringUtils.random(3, true, true)!!
