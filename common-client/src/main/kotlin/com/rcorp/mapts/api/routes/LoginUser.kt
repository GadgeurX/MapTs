package com.rcorp.mapts.api.routes

import com.rcorp.mapts.api.repo.UserRepository
import com.rcorp.mapts.model.user.User

class LoginUser(private val userRepository: UserRepository) {
    fun run(user: User, callback: (User?) -> Unit) = userRepository.loginUser(user, callback)
}
