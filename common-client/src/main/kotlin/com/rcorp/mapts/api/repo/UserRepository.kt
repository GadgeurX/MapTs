package com.rcorp.mapts.api.repo

import com.rcorp.mapts.ApplicationDispatcher
import com.rcorp.mapts.api.ApiUtils
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

class UserRepository {

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    fun registerUser(user: User, callback: (Boolean) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            val result = registerFromServer(user)
            callback(result)
        }
    }

    private suspend fun registerFromServer(user: User): Boolean {
        return try {
            client.post<String> {
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = ApiUtils.host
                    port = 9999
                    encodedPath = "user"
                }
                body = user
            }
            true
        } catch (e: Exception) {
            println(e.message)
            false
        }
    }

    fun loginUser(user: User, callback: (User?) -> Unit) {
        println(user)
        GlobalScope.launch(ApplicationDispatcher) {
            val result = loginFromServer(user)
            callback(result)
        }
    }

    private suspend fun loginFromServer(user: User): User? {
        return try {
            client.post<User> {
                contentType(ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTP
                    host = ApiUtils.host
                    port = 9999
                    encodedPath = "user/login"
                }
                body = user
            }
        } catch (e: Exception) {
            println(e)
            println(e.message)
            println(e.cause)
            null
        }
    }
}