package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

import com.example.stocks.*;

suspend fun main() {
	var resolver: StockResolver = MoexResolver()

	print(resolver.getStocks());

	print(resolver.getCurrentPrice("A-RM"))
}
