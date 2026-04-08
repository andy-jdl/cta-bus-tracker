package com.opengallery

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val client = configureClient()
    configureSerialization()
    configureSockets(client)
    configureRouting(client)
    monitor.subscribe(ApplicationStopped) {
        client.close()
    }
}
