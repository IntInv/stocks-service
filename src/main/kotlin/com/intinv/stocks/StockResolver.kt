package com.intinv.stocks;

interface StockResolver {
	suspend fun getStocks(): List<Ticket>
	suspend fun getCurrentPrices(secIds: List<String>): Map<String, Double>
};
