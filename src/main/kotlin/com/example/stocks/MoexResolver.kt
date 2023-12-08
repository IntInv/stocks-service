package com.example.stocks;

import io.ktor.client.*
import io.ktor.client.engine.cio.*;
import io.ktor.client.request.*;
import io.ktor.client.statement.*;
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf

class MoexResolver: StockResolver {
	
	private val client = HttpClient(CIO)

	private fun parseStockList(json: String): List<String> {
		val stocks: MutableList<String> = mutableListOf<String>()
		val mapper = jacksonObjectMapper()
		val root: JsonNode = mapper.readTree(json)

		val secs = root.path("securities")
		val data = secs.path("data")

		if (data.isArray()) {
			for (record in data)
				stocks.add(record[1].asText())
		}

		return stocks
	}

	private fun parseStockPrices(json: String): Map<String, Double> {
		val prices: MutableMap<String, Double> = mutableMapOf()
		val mapper = jacksonObjectMapper()
		val root: JsonNode = mapper.readTree(json)

		val price_node = root.path("currentprices")
		val data = price_node.path("data")
		
		if (data.isArray()) {
			for (record in data)
				prices.put(record[2].asText(), record[4].asDouble())
		}

		return prices
	}

	override suspend fun getStocks(): List<String> {
		val response: HttpResponse = client.get("https://iss.moex.com/iss/securities.json")

		return parseStockList(response.bodyAsText())
	}

	override suspend fun getCurrentPrice(secId: String): Double {
		val response: HttpResponse = client.get("https://iss.moex.com/iss/statistics/engines/stock/currentprices.json")

		val pricesMap: Map<String, Double> = parseStockPrices(response.bodyAsText())

		return pricesMap.get(secId) ?: 0.0
	}

	// TODO
	override suspend fun getCurrentPrice(secId: List<String>): Double {
		return 2.0
	}
}
