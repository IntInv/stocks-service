package com.intinv.msgs;

import com.intinv.stocks.Ticket;

data class InfoResponse (
	var stocks: List<Ticket>
) : Response()
