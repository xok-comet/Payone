package com.comet.payone.callback

import com.google.gson.JsonObject

abstract class POMCallback {
    abstract fun status(poStatus: String?)
    abstract fun message(poMessageResult: JsonObject?)
}
