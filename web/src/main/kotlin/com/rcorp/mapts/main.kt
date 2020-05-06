package com.rcorp.mapts

import com.rcorp.mapts.loginPage.LoginRegisterPage
import com.rcorp.mapts.mapPage.MapPage

external val app: Any
external val Vue: Any
external val mapboxgl: Any

fun main(args: Array<String>) {
    LoginRegisterPage()
    MapPage()
}