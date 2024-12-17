package com.example.todoapp

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class MyHostApduService : HostApduService() {
    companion object {
        var dataToSend: String? = null
    }
    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray? {
        if (commandApdu == null) return null
        Log.d("HCE", "Received APDU: ${commandApdu.toHexString()}")

        // Respond with a custom message to the APDU
        val response = dataToSend?.toByteArray() ?: return null
        Log.d("HCE", "Sending response: ${response.toHexString()}")
        return response
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Deactivated: reason=$reason")
    }

    // Hàm tiện ích để chuyển đổi ByteArray thành chuỗi hex
    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}