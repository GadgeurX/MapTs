package com.rcorp.mapts

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rcorp.mapts.controllers.territory
import com.rcorp.mapts.controllers.user
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.Routing

fun Application.module() {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter(buildGson()))
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
    }

    install(Routing) {
        territory()
        user()
    }
}

private fun buildGson(): Gson {
    return GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create()
}