package com.comet.payone.data


data class POStore (
    var mcid: String? = null,
    var country: String? = null,
    val province: String? = null,
    val subscribeKey: String,
    val terminalid: String? = null
)