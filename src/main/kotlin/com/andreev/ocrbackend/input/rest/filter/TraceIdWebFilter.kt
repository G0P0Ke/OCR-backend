package com.andreev.ocrbackend.input.rest.filter

import org.slf4j.MDC
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

const val TRACE_ID_HEADER = "X-Trace-Id"

class TraceIdWebFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val traceId = httpRequest.getHeader(TRACE_ID_HEADER)
        if (traceId != null) {
            MDC.put("traceId", traceId)
        }
        try {
            chain.doFilter(request, response)
        } finally {
            MDC.remove("traceId")
        }
    }

}