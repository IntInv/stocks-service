package com.example.msgs;

data class Response (
	var id: Long,
	var err: Long,
) {
	var result: List<String>? = emptyList()
}
