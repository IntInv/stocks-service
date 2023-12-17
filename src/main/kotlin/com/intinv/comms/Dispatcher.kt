package com.intinv.comms;

import com.intinv.msgs.*;
import com.intinv.stocks.StockResolver;
import com.intinv.stocks.Ticket;
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.channels.Channel
import com.fasterxml.jackson.module.kotlin.readValue

class Dispatcher(val channel: Channel<String>, val redis: Redis, val resolver: StockResolver) {

	val mapper = jacksonObjectMapper()

	suspend fun run() {
		while(true) {
			var message: String = channel.receive()
			var request: Request = mapper.readValue(message)

			var response: String = unknownMethod(request)

			when(request.method) {
				"getStocks" -> response = getStocks(request)
				"getPrices" -> response = getPrices(message)
			}

			sendResponse(response)
		}
	}

	fun unknownMethod(request: Request): String {
		var response: Response = Response(request.id, 1, "unknown")
		return mapper.writeValueAsString(response)
	}

	suspend fun getPrices(message: String): String {

		var request: PriceRequest = mapper.readValue(message)

		var map = resolver.getCurrentPrices(request.stocks)

		var response: PriceResponse = PriceResponse(map)
		response.id = request.id
		response.method = request.method
		response.err = 0

		return mapper.writeValueAsString(response)
	}

	suspend fun getStocks(request: Request): String {

		val list = resolver.getStocks()

		val response: InfoResponse = InfoResponse(list)
		response.id = request.id
		response.err = 0
		response.method = request.method

		return mapper.writeValueAsString(response)
	}

	fun sendResponse(response: String) {
		redis.send(response)
	}
}
