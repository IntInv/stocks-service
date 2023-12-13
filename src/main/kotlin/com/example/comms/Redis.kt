package com.example.comms;

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.channels.Channel
import kotlin.concurrent.thread

class Redis(
	val channel: Channel<String>,
	val hostname: String = "localhost",
	val port: Int = 6379
) {

	val redisChannel = "api.stocks"

	val pool = JedisPool(hostname, port)
	val jedisPubSub = object : JedisPubSub() {
		override fun onMessage(channel: String?, message: String?) {
			runBlocking {
				this@Redis.channel.send(message ?: "null")
			}
		}
	}

	public fun run() {
		thread {
			pool.resource.subscribe(jedisPubSub, "api.rest")
		}
	}

	public fun send(message: String) {
		pool.resource.publish(redisChannel, message)
	}
}
