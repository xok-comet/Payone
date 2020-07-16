package com.comet.payone

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.comet.payone.callback.POMCallback
import com.comet.payone.data.POStore
import com.comet.payone.data.POTransaction
import com.google.gson.JsonObject
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        POManager.share(this)!!.initStore("mch5e68b5cf1691c", "LA", "VTE","sub-c-91489692-fa26-11e9-be22-ea7c5aada356", "101")
        val qrcode = POManager.share(this)!!.buildQrcode("1", "418", "123", "test")
        POManager.share(this)!!.Start( object : POMCallback() {
            override fun status(poStatus: String?) {
                Log.d("TAG", "status: "+poStatus)
            }

            override fun message(poMessageResult: JsonObject?) {
                Log.d("TAG", "status: "+poMessageResult)
            }
        })
    }
}