package com.rcorp.mapts.presenter.login

import com.rcorp.mapts.UIDispatcher
import com.rcorp.mapts.api.repo.UserRepository
import com.rcorp.mapts.api.routes.LoginUser
import com.rcorp.mapts.model.user.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginPresenter() {
    private val loginUser: LoginUser = LoginUser(UserRepository())
    private lateinit var view: LoginContract.View

    fun start(view: LoginContract.View) {
        this.view = view
    }

    fun loginUser(user: User) {
        try {
            loginUser.run(user) { user ->
                GlobalScope.launch(UIDispatcher) {
                    if (user != null)
                        view.onLoginSuccess(user)
                    else
                        view.onLoginFail()
                }
            }
        } catch (e: Exception) {
            view.onLoginFail()
        }
    }
}