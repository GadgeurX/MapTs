package com.rcorp.mapts.model.territory

import kotlinx.serialization.Serializable

@Serializable
data class Territory(var id: Long, var name: String, var geometry: String?)