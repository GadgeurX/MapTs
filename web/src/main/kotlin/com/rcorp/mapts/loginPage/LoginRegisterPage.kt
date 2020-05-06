package com.rcorp.mapts.loginPage

import com.rcorp.mapts.loginView
import com.rcorp.mapts.mapboxgl
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.id
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe
import kotlin.browser.window

class LoginRegisterPage {

    init {
        window.asDynamic().loginRegisterPage = this
        LoginView()
        RegisterView()
    }

    @JsName("template")

    val template = createHTML().div {
        div { id = "map" }
        div("login-form") {
            h1("text-center") {
                attributes["style"]= "margin-bottom: 40%;"
                + "MapTS"
            }
            loginView {
                attributes["v-if"] = "!register"
                attributes["v-on:register"] = "register = !register"
            }
            unsafe {
                raw("""<registerView v-if="register"></registerView>""")
            }
        }
    }
    //language=Vue
    /*"""<div><div id='map'></div><div class="login-form">
        <h1 class="text-center" style="margin-bottom: 40%;">MapTS</h1>
        <loginView v-if="!register" v-on:register='register = !register'></loginView>
        <registerView v-if="register"></registerView>
    </div></div>"""*/


    @JsName("data")
    fun data(): Any = object {
        @JsName("register")
        var register = false
    }

    @JsName("mounted")
    fun mounted() {
        mapboxgl.asDynamic().accessToken = "pk.eyJ1IjoiZ2FkZ2V1cngiLCJhIjoiY2sxMHY4andrMDRhZDNubzBseXIyNW8yNyJ9.ZX9pc8gCJtGmmkvrtsqCDg"
        js("new mapboxgl.Map({\n" +
                "        style: 'mapbox://styles/gadgeurx/ck8errj47070m1itchg9vv0gm',\n" +
                "        center: [-0.8769057444109194, 47.0912337940309],\n" +
                "        zoom: 8.3,\n" +
                "        pitch: 45,\n" +
                "        bearing: -17.6,\n" +
                "        container: 'map',\n" +
                "        antialias: true\n" +
                "    });")
    }
}