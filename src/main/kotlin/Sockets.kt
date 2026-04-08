package com.opengallery

import com.opengallery.model.Priority
import com.opengallery.model.Task
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.url
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(client: HttpClient) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val busRouteArrivalTimeFlow = MutableSharedFlow<String>(replay = 1)

    routing {
        webSocket("/bustime") {
            try {
                val response = client.get {
                    url {
                        appendPathSegments("gettime")
                    }
                }.bodyAsText()

                send(response)
                delay(1.seconds)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        webSocket("/bustime/arrivals") {
            val rt = call.request.queryParameters["rt"].toString()
            val stpid = call.request.queryParameters["stpid"].toString()

            try {
                while(true) {
                    val response = client.get {
                        url {
                            appendPathSegments("getpredictions")
                            parameters.append("rt", rt)
                            parameters.append("stpid", stpid)
                        }
                    }.bodyAsText()
                    busRouteArrivalTimeFlow.emit(response)
                    send(response)
                    delay(30.seconds)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                close()
            }
        }
    }
}
