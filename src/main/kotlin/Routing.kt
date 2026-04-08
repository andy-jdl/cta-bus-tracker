package com.opengallery

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import io.ktor.http.parameters
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(client: HttpClient) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/bustime/gettime") {
            try {
                // val response = HttpClient().use { client -> client.get(url) }.bodyAsText()
                val response = client.get {
                    url {
                        appendPathSegments("gettime")
                    }
                }.bodyAsText()
                call.respondText(response)
            } catch (e: Exception) {
                call.respondText("Server Error: $e")
            }
        }

        get("/bustime/vehicles") {
            val rt = call.request.queryParameters["rt"] ?: ""
            println(rt)
            try {
                val response = client.get {
                    url {
                        appendPathSegments("getvehicles")
                        parameters.append("rt", rt)
                    }
                }.bodyAsText()
                call.respondText(response)
            } catch (e: Exception) {
                call.respondText("Server Error: $e")
            }
        }

        get("/bustime/directions") {
            val rt = call.request.queryParameters["rt"] ?: ""
            try {
                val response = client.get {
                    url {
                        appendPathSegments("getdirections")
                        parameters.append("rt", rt)
                    }
                }.bodyAsText()
                call.respondText(response)
            } catch (e: Exception) {
                call.respondText("Server Error: $e")
            }
        }

        get("/bustime/stops") {
            val rt = call.request.queryParameters["rt"] ?: ""
            val dir = call.request.queryParameters["dir"] ?: ""
            try {
                val response = client.get {
                    url {
                        appendPathSegments("getstops")
                        parameters.append("rt", rt)
                        parameters.append("dir", dir)
                    }
                }.bodyAsText()
                call.respondText(response)
            } catch (e: Exception) {
                call.respondText("Server Error: $e")
            }
        }

        get("/bustime/predictedArrival") {
            val rt = call.request.queryParameters["rt"] ?: ""
            val stopId = call.request.queryParameters["stpid"] ?: ""
            try {
                val response = client.get {
                    url {
                        appendPathSegments("getpredictions")
                        parameters.append("rt", rt)
                        parameters.append("stpid", stopId)
                    }
                }.bodyAsText()
                call.respondText(response)
            } catch (e: Exception) {
                call.respondText("Server Error: $e")
            }
        }

        // Try to access `/static/index.html`
        staticResources("/static", "static")
    }
}
