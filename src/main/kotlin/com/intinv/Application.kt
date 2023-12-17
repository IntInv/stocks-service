package com.intinv

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

import com.intinv.stocks.*;
import com.intinv.comms.Redis
import com.intinv.comms.Dispatcher
import kotlinx.coroutines.channels.Channel

suspend fun main() {
	val channel = Channel<String>()
	val redis = Redis(channel)
	val dispatcher = Dispatcher(channel, redis, MoexResolver())

	redis.run()
	dispatcher.run()
}
