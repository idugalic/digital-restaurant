package com.drestaurant.configuration

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class MyCorsFilter : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val response = res as HttpServletResponse
        response.setHeader("Access-Control-Allow-Origin", "*")

        // without this header jquery.ajax calls returns 401 even after successful login and SSESSIONID being succesfully stored.
        response.setHeader("Access-Control-Allow-Credentials", "true")

        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
        response.setHeader("Access-Control-Max-Age", "3600")
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Authorization, Origin, Content-Type, Version")
        response.setHeader("Access-Control-Expose-Headers", "X-Requested-With, Authorization, Origin, Content-Type")

        val request = req as HttpServletRequest
        if (request.method != "OPTIONS") {
            chain.doFilter(req, res)
        } else {
            // do not continue with filter chain for options requests
        }
    }

    override fun destroy() {

    }

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
    }
}