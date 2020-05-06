package com.rcorp.mapts.presenter.login

import com.rcorp.mapts.model.user.User

class LoginContract {
    interface View {
        fun onLoginSuccess(user: User)

        fun onLoginFail()
    }
}