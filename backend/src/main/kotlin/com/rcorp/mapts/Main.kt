package com.rcorp.mapts

import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database


fun main(args: Array<String>) {
    Database.connect("jdbc:postgresql://192.168.0.46/MapTS", driver = "org.postgresql.Driver", user = "postgres", password = "98956263")
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}