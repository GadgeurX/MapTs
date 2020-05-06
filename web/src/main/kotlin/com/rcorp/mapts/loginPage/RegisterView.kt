package com.rcorp.mapts.loginPage

import com.rcorp.mapts.Vue
import com.rcorp.mapts.model.user.User
import com.rcorp.mapts.presenter.register.RegisterContract
import com.rcorp.mapts.presenter.register.RegisterPresenter

class RegisterView {
    @JsName("template")
    //language=Vue
    val template = """<transition name="fade">
        <form style="position: absolute;width: 374px;" id="register-form">
            <h2 class="text-center" >Register</h2>
            <div class="form-group">
                <input type="text" class="form-control" v-model="user.login" placeholder="Username" required="required">
            </div>
            <div class="form-group">
                <input type="text" class="form-control" v-model="user.email" placeholder="Email" required="required">
            </div>
            <div class="form-group">
                <input type="password" class="form-control" v-model="user.password" placeholder="Password" required="required">
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-primary btn-block" v-on:click="${'$'}data.onRegisterClick()">Register</button>
            </div>
            <p v-if="error" class="text-center" style="color: #AA1111">Cannot register, please verify your info</p>
        </form>
    </transition>"""


    init {
        Vue.asDynamic().component("registerView", this)
    }

    @JsName("data")
    fun data(): Any = object : RegisterContract.View {

        @JsName("presenter")
        val presenter = RegisterPresenter()

        @JsName("user")
        val user = User()

        @JsName("error")
        var error = false

        init {
            presenter.start(this)
        }

        @JsName("onRegisterClick")
        fun onRegisterClick() {
            presenter.registerUser(user)
        }

        override fun onRegisterSuccess() {
            error = false
            console.log("Success to register")
        }

        override fun onRegisterFail() {
            error = true
            console.log("Fail to register")
        }
    }

}