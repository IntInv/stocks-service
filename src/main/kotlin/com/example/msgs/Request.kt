package com.example.msgs;

data class Request (
	var id: Long,
	var method: String,
) {
	var args: List<String>? = emptyList()
}
