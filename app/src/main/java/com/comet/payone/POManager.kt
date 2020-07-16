package com.comet.payone

import android.content.Context
import android.util.Log
import com.comet.payone.callback.POMCallback
import com.comet.payone.data.POQrcodeImage
import com.comet.payone.data.POStore
import com.comet.payone.data.POTransaction
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.enums.PNStatusCategory
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult
import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult
import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult
import java.io.IOException
import java.util.*


class POManager(applicationContext: Context) {
    lateinit var mcid: String
    lateinit var country: String
    lateinit var province: String
    lateinit var subscribeKey: String
    lateinit var terminalid: String
    lateinit var uuid: String
    fun buildQrcode(
        amount: String,
        currency: String,
        invoiceid: String,
        description: String
    ): String {
        try {
            var store =
                POStore(this.mcid, this.country, this.province, this.subscribeKey, this.terminalid)
            var transation =
                POTransaction().createUniqueTransaction(amount, currency, invoiceid, description)
            var qrCodeData = POQrcodeGenerator(store, transation).getQRCodeInfo()
            if (qrCodeData != null) {
                this.uuid = qrCodeData
            }
            return qrCodeData!!;
        } catch (e: IOException) {
            return "Store is not init yet"
        }
    }

    fun initStore(
        mcid: String,
        country: String,
        province: String,
        subscribeKey: String,
        terminalid: String
    ) {
        this.mcid = mcid
        this.country = country
        this.province = province
        this.subscribeKey = subscribeKey
        this.terminalid = terminalid
    }

    fun Start(listener: POMCallback) {
        val mcid: String = this.mcid
        val uuid: String = this.uuid
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = this.subscribeKey
        pnConfiguration.publishKey = this.subscribeKey
        pnConfiguration.uuid = this.uuid
        Log.d("TAG", this.uuid)
        val pubnub = PubNub(pnConfiguration)
        val channelName = "uuid-$mcid-$uuid"
        pubnub.addListener(object : SubscribeCallback() {
            override fun status(pubnub: PubNub, status: PNStatus) {
                when (status.category) {
                    PNStatusCategory.PNDisconnectedCategory -> listener.status("Disconnected")
                    PNStatusCategory.PNConnectedCategory -> listener.status("OK Connected")
                    PNStatusCategory.PNTimeoutCategory -> listener.status("Connection timeout")
                    PNStatusCategory.PNReconnectedCategory -> listener.status("Reconnected")
                }
            }

            override fun message(pubnub: PubNub, message: PNMessageResult) {
                val str = message.message.asString
                val convertedObject =
                    Gson().fromJson(str, JsonObject::class.java)
                listener.message(convertedObject)
            }

            override fun presence(
                pubnub: PubNub,
                presence: PNPresenceEventResult
            ) {
            }

            override fun signal(
                pubnub: PubNub,
                pnSignalResult: PNSignalResult
            ) {
            }

            override fun user(pubnub: PubNub, pnUserResult: PNUserResult) {}
            override fun space(pubnub: PubNub, pnSpaceResult: PNSpaceResult) {}
            override fun membership(
                pubnub: PubNub,
                pnMembershipResult: PNMembershipResult
            ) {
            }

            override fun messageAction(
                pubnub: PubNub,
                pnMessageActionResult: PNMessageActionResult
            ) {
            }
        })
        pubnub.subscribe().channels(Arrays.asList(channelName)).execute()
    }

    companion object {
        private var instance: POManager? = null
        fun share(app: Context): POManager? {
            if (instance == null) instance = POManager(app)
            return instance
        }
    }
}
