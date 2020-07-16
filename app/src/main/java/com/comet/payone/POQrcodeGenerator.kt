package com.comet.payone

import com.comet.payone.data.POQrcodeImage
import com.comet.payone.data.POStore
import com.comet.payone.data.POTransaction
import java.text.DecimalFormat
import java.text.NumberFormat


class POQrcodeGenerator internal constructor(Store: POStore, Transaction: POTransaction) {
    var Store: POStore
    var Transaction: POTransaction
    private fun crc16ccitt(sStr: String): String {
        var crc = 0xFFFF
        val polynomial = 0x1021
        val bytes: ByteArray = sStr.toByteArray()

        for (b in bytes) {
            for (i in 0..7) {
                val bit = ((b.toInt() shr (7 - i)) and 1) == 1
                val c15 = (crc shr 15 and 1) == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }
        crc = crc and 0xffff
        return Integer.toHexString(crc)

    }

    private fun buildqr(qrcodeRaw: Map<String, String>): String {
        val formatter: NumberFormat = DecimalFormat("00")
        var res = ""
        for ((key, value) in qrcodeRaw) {
            res += key
            res += formatter.format(value.length.toLong())
            res += value
        }
        return res
    }

    fun getQRCodeInfo(): String? {
        if (Store == null || Transaction == null) {
            return null
        }
        val mcc = "4111"
        val invoiceId: String? = Transaction.invoiceid
        val transactionid: String? = Transaction.reference
        val description: String? = Transaction.description
        val PayloadFormatIndicator = "01"
        val PointInitiationMethod = "11"
        val amount: String? = Transaction.amount
        val mcid: String? = Store.mcid
        val terminalid: String? = Store.terminalid
        val country: String? = Store.country
        val province: String? = Store.province
        val ccy: String? = Transaction.currency
        val inn: String = "BCEL"
        val applicationid: String = "ONEPAY"
        val StoreDataRaw: Map<String, String> =
            mapOf("00" to inn, "01" to applicationid, "02" to mcid!!)

        val StoreData = buildqr(StoreDataRaw)
        val DataObjectsAdditionalRaw: Map<String, String> = mapOf(
            "01" to invoiceId!!,
            "05" to transactionid!!,
            "07" to terminalid!!,
            "08" to description!!
        )

        val DataObjectsAdditional = buildqr(DataObjectsAdditionalRaw)

        val qrcodeRaw = mapOf(
            "00" to PayloadFormatIndicator,
            "01" to PointInitiationMethod,
            "33" to StoreData,
            "52" to mcc,
            "53" to ccy!!,
            "54" to amount!!,
            "58" to country!!,
            "60" to province!!,
            "62" to DataObjectsAdditional
        )
        val CyclicRedundancyCheckRaw = mapOf(
            "63" to crc16ccitt((buildqr(qrcodeRaw) + "6304"))
        )
        var qrcode = buildqr(qrcodeRaw)
        qrcode += buildqr(CyclicRedundancyCheckRaw)
        return qrcode
    }

    init {
        this.Store = Store
        this.Transaction = Transaction
    }
}
