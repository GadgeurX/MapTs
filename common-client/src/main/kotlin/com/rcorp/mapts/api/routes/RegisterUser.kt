package com.rcorp.mapts.api.routes

import com.rcorp.mapts.api.repo.UserRepository
import com.rcorp.mapts.model.user.User

class RegisterUser(private val userRepository: UserRepository) {
    fun run(user: User, callback: (Boolean) -> Unit) = userRepository.registerUser(user, callback)
}
