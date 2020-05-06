package com.rcorp.mapts

import com.rcorp.mapts.loginPage.LoginView
import kotlinx.html.*

class LOGINVIEW(consumer: TagConsumer<*>) :
        HTMLTag("loginView", consumer, emptyMap(),
                inlineTag = true,
                emptyTag = false), HtmlInlineTag

fun DIV.loginView(block: LOGINVIEW.() -> Unit = {}) {
    LOGINVIEW(consumer).visit(block)
}