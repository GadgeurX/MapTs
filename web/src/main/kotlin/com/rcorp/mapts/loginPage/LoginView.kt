package com.rcorp.mapts.loginPage

import com.rcorp.mapts.Vue
import com.rcorp.mapts.app
import com.rcorp.mapts.model.user.User
import com.rcorp.mapts.presenter.login.LoginContract
import com.rcorp.mapts.presenter.login.LoginPresenter



class LoginView {

    @JsName("template")
    //language=Vue
    val template = """<transition name="fade">
    <form id="login-form" style="position: absolute;width: 374px;">
    <h2 class="text-center">Log in</h2>
    <div class="form-group">
    <input type="text" v-model="user.login" class="form-control" placeholder="Username" required="required">
    </div>
    <div class="form-group">
    <input type="password" v-model="user.password" class="form-control" placeholder="Password" required="required">
    </div>
    <div class="form-group">
    <button type="button" class="btn btn-primary btn-block" v-on:click="${'$'}data.onLoginClick()">Log in</button>
    </div>
    <p v-if="error" class="text-center" style="color: #AA1111">Cannot login, please verify your info</p>
    <div class="clearfix">
    <label class="pull-left checkbox-inline"><input type="checkbox"> Remember me</label>
    </div>
    <p class="text-center" style="position: absolute"><a href="#" v-on:click="${'$'}emit('register')">Create an Account</a></p>
    </form>
    </transition>"""

    init {
        Vue.asDynamic().component("loginView", this)
    }

    @JsName("data")
    fun data(): Any = object: LoginContract.View {
        @JsName("user")
        val user: User = User()

        @JsName("error")
        var error = false

        @JsName("presenter")
        val presenter = LoginPresenter()

        init {
            presenter.start(this)
        }

        @JsName("onLoginClick")
        fun onLoginClick() {
            presenter.loginUser(user)
        }

        override fun onLoginSuccess(user: User) {
            error = false
            app.asDynamic().currentRoute = "/map"
            console.log("Success to login")
        }

        override fun onLoginFail() {
            error = true
            console.log("Fail to login")
        }
    }


}