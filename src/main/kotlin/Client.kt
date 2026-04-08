package com.opengallery

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json


fun Application.configureClient(): HttpClient {
    val apiKey = environment.config
        .propertyOrNull("ktor.ctaapi.key")
        ?.getString() ?: throw IllegalStateException("CTA API key not found")

    return HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.INFO
        }
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "www.ctabustracker.com"
                encodedPath = "/bustime/api/v3/"
                parameters.append("key", apiKey)
                parameters.append("format", "json")
            }
        }
    }
}