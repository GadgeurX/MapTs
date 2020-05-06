package com.rcorp.mapts.controllers

import com.rcorp.mapts.data.db.UserDao
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Routing
import io.ktor.routing.route
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post

fun Routing.user() {

    route("user") {
        post {
            println("Register user")
            try {
                val result = UserDao.register(call.receive())
                call.respond(result)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Exception(e.message))
            }

        }
    }
    route("user/login") {
        post {
            try {
                call.respond(UserDao.login(call.receive()) ?: Exception("Password incorrect or account not found"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Exception(e.message))
            }
        }
    }
}