package com.rcorp.mapts.api.repo

import com.rcorp.mapts.ApplicationDispatcher
import com.rcorp.mapts.api.ApiUtils
import com.rcorp.mapts.model.territory.Territory
import com.rcorp.mapts.model.user.User
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentLength
import io.ktor.http.contentType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json

class TerritoryRepository {

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    fun getTerritories(callback: (List<Territory>?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            val result = getTerritoriesFromServer()
            callback(result)
        }
    }

    private suspend fun getTerritoriesFromServer(): List<Territory>? {
        return try {
            Json.parse(Territory.serializer().list,client.get {
                url {
                    protocol = URLProtocol.HTTP
                    host = ApiUtils.host
                    port = 9999
                    encodedPath = "territories"
                }
            })
        } catch (e: Exception) {
            println("ERRRORRR")
            println(e.cause)
            println(e.toString())
            null
        }
    }
}