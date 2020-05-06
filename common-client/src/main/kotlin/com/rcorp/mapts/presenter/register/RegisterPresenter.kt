package com.rcorp.mapts.presenter.register

import com.rcorp.mapts.UIDispatcher
import com.rcorp.mapts.api.repo.UserRepository
import com.rcorp.mapts.api.routes.RegisterUser
import com.rcorp.mapts.model.user.User
import com.rcorp.mapts.presenter.map.MapContract
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterPresenter {
    private val registerUser: RegisterUser = RegisterUser(UserRepository())
    private lateinit var view: RegisterContract.View

    fun start(view: RegisterContract.View) {
        this.view = view
    }

    fun registerUser(user: User) {
        try {
            registerUser.run(user) { result ->
                GlobalScope.launch(UIDispatcher) {
                    if (result)
                        view.onRegisterSuccess()
                    else
                        view.onRegisterFail()
                }
            }
        } catch (e: Exception) {
            view.onRegisterFail()
        }
    }
}