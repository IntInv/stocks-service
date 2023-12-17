package com.intinv.stocks;

import io.ktor.client.*
import io.ktor.client.engine.cio.*;
import io.ktor.client.request.*;
import io.ktor.client.statement.*;
import io.ktor.util.date.getTimeMillis
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.JsonNode
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf

class MoexResolver: StockResolver {

	private var cachedPrices: Map<String, Double> = mapOf()
	private var pricesTime: Long = 0
	private val PRICES_CACHE_VALID = 10 * 60 * 1000

	private var cachedInfo: List<Ticket> = listOf()
	private var infoTime: Long = 0
	private val INFO_CACHE_VALID = 20 * 60 * 1000
	
	private fun parseStockList(json: String): List<Ticket> {
		val stocks: MutableList<Ticket> = mutableListOf<Ticket>()
		val mapper = jacksonObjectMapper()
		val root: JsonNode = mapper.readTree(json)

		val secs = root.path("securities")
		val data = secs.path("data")

		if (data.isArray()) {
			for (record in data)
				stocks.add(
					Ticket(
						record[1].asText(),
						record[2].asText()
					)
				)
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

	suspend fun updateInfo() {
		if (infoTime > 0 && (getTimeMillis() - infoTime < INFO_CACHE_VALID))
			return

		val response: HttpResponse

		HttpClient(CIO).use {
			response = it.get("https://iss.moex.com/iss/securities.json")
		}

		cachedInfo = parseStockList(response.bodyAsText())
		infoTime = getTimeMillis()
	}

	suspend fun updatePrices() {
		if (pricesTime > 0 && (getTimeMillis() - pricesTime < PRICES_CACHE_VALID))
			return

		val response: HttpResponse
		HttpClient(CIO).use {
			response = it.get("https://iss.moex.com/iss/statistics/engines/stock/currentprices.json")
		}

		cachedPrices = parseStockPrices(response.bodyAsText())
		pricesTime = getTimeMillis()
	}

	override suspend fun getStocks(): List<Ticket> {
		updateInfo()

		return cachedInfo
	}

	override suspend fun getCurrentPrices(secIds: List<String>): Map<String, Double> {
		updatePrices()

		var map = cachedPrices.filter {
			(key, _) -> secIds.contains(key)
		}

		return map
	}
}
