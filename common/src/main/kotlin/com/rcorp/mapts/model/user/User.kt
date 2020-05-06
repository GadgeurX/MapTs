package com.rcorp.mapts.model.user

import kotlinx.serialization.*

@Serializable
data class User(var id: Int? = null, var login: String? = null, var email: String? = null, var password: String? = null, var token: String? = null) {

}