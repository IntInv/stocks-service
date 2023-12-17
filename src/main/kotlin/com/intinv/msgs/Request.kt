package com.intinv.msgs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
open class Request (
	var id: Long = 0,
	var method: String = "unknown",
) { }
