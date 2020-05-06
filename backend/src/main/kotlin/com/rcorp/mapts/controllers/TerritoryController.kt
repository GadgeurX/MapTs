package com.rcorp.mapts.controllers

import com.rcorp.mapts.data.db.TerritoryDao
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import com.rcorp.mapts.model.*
import io.ktor.response.respond

fun Routing.territory() {

    route("territories") {
        get { call.respond(TerritoryDao.getAllTerritories()) }
    }
}