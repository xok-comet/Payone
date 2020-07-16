package com.comet.payone.data

import java.util.*

data class POTransaction(
    var invoiceid: String? = null,
    var amount: String? = null,
    var currency: String? = null,
    var description: String? = null,
    var reference: String? = null
){
    fun createUniqueTransaction(
        amount: String,
        currency: String,
        invoiceid: String,
        description: String?
    ): POTransaction {
        val Pot = POTransaction()
        Pot.reference = UUID.randomUUID().toString().toLowerCase()
        Pot.invoiceid = invoiceid ?: "123"
        Pot.amount = amount
        Pot.currency = currency
        Pot.description = description ?: "comet"
        return Pot
    }
}