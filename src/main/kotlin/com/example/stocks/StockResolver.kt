package com.example.stocks;

interface StockResolver {
	suspend fun getStocks(): List<String>
	suspend fun getCurrentPrice(secId: String): Double
	suspend fun getCurrentPrice(secId: List<String>): Double
};
