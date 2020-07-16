package com.comet.payone.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class POStore (
    var mcid: String? = null,
    var country: String? = null,
    val province: String? = null,
    val subscribeKey: String,
    val terminalid: String? = null
)