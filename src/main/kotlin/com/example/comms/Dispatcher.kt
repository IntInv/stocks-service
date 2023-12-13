package com.example.comms;

import com.example.msgs.*;
import com.example.stocks.StockResolver;
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.channels.Channel
import com.fasterxml.jackson.module.kotlin.readValue

class Dispatcher(val channel: Channel<String>, val redis: Redis, val resolver: StockResolver) {

	val mapper = jacksonObjectMapper()

	suspend fun run() {
		while(true) {
			var message: String = channel.receive()
			var request: Request = mapper.readValue(message)
			var response: Response = dispatch(request)
			sendResponse(response)
		}
	}

	fun unknownMethod(request: Request): Response {
		return Response(request.id, 1)
	}
	

	fun getStocks(request: Request): Response {
		return Response(request.id, 0)
	}

	fun getStocksInfo(request: Request): Response {
		return Response(request.id, 0)
	}

	fun dispatch(request: Request): Response {
		var response: Response = unknownMethod(request)
		when (request.method) {
			"getStocks" -> response = getStocks(request)
			"getStocksInfo" -> response = getStocksInfo(request)
		}
		return response
	}

	fun sendResponse(response: Response) {
		val textResponse = mapper.writeValueAsString(response)
		redis.send(textResponse)
	}
}
