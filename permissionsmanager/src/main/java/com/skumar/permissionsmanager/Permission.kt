package com.skumar.permissionsmanager

import android.Manifest
import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build

/**
 * @author s.kumar on 18/07/2017.
 *
 * Copyright (C)  18/07/2017 Sughosh Krishna Kumar
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE SUGHOSH KRISHNA KUMAR BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

@TargetApi(Build.VERSION_CODES.M)
open class Permission(val requestCode: Int = -10) {
    var isGranted: Boolean = false
    var hasAskedPermission: Boolean
        get() {
            if (getString() != null && preferences != null){
                return preferences?.getBoolean(getString(), false) ?: false
            }
            return hasAsked
        }
        set(value) {
            if (getString() != null && preferences != null) {
                preferences?.edit()?.putBoolean(getString(), value)?.apply()
            } else {
                hasAsked = value
            }
        }

    var preferences: SharedPreferences? = null
    var neverAskPermission: Boolean
        get() = preferences?.getBoolean(ASK_PERMISSION, false) ?: neverAsk
        set(value) {
            if (preferences != null)
                preferences?.edit()?.putBoolean(ASK_PERMISSION, value)?.apply()
            else
                neverAsk = true
        }
    var permissionArray: Array<String> = arrayOf()
    private var neverAsk = false
    private var hasAsked = false

    companion object {
        val ASK_PERMISSION = "permission.ask"

        val CAMERA = Permission(0)
        val LOCATION = Permission(1)
        val MICROPHONE = Permission(2)
        val CALENDAR = Permission(3)
        val CONTACTS = Permission(4)
        val STORAGE = Permission(5)
        val PHONE = Permission(6)
        val BODY_SENSOR = Permission(7)
        val SMS = Permission(8)

        fun fromPermissions(permissionString: Array<out String>): Permission? {
            when(permissionString) {
                arrayOf(Manifest.permission.CAMERA) -> return CAMERA
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION) -> return LOCATION
                arrayOf(Manifest.permission.RECORD_AUDIO) -> return MICROPHONE
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS) -> return CONTACTS
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) -> return STORAGE
                arrayOf(Manifest.permission.CALL_PHONE) -> return PHONE
                arrayOf(Manifest.permission.BODY_SENSORS) -> return BODY_SENSOR
                arrayOf(Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.BROADCAST_SMS) -> return SMS
            }
            return null
        }
    }

    fun getPermissionString(): Array<String> {
        when(this) {
            CAMERA -> permissionArray = arrayOf(Manifest.permission.CAMERA)
            LOCATION -> permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            MICROPHONE -> permissionArray = arrayOf(Manifest.permission.RECORD_AUDIO)
            CALENDAR -> permissionArray = arrayOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR)
            CONTACTS -> permissionArray = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
            STORAGE -> permissionArray = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            PHONE -> permissionArray = arrayOf(Manifest.permission.CALL_PHONE)
            BODY_SENSOR -> permissionArray = arrayOf(Manifest.permission.BODY_SENSORS)
            SMS -> permissionArray = arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.BROADCAST_SMS)
        }
        return permissionArray
    }

    fun getString(): String? {
        when(this) {
            CAMERA -> return "CAMERA_PERMISSION"
            LOCATION -> return "LOCATION_PERMISSION"
            MICROPHONE -> return "MICROPHONE_PERMISSION"
            CALENDAR -> return "CALENDAR_PERMISSION"
            CONTACTS -> return "CONTACT_PERMISSION"
            STORAGE -> return "STORAGE_PERMISSION"
            PHONE -> return "PHONE_PERMISSION"
            BODY_SENSOR -> return "BODY_SENSOR_PERMISSION"
            SMS -> return "SMS_PERMISSION"
        }
        return null
    }

    override fun equals(other: Any?): Boolean {
        if (other is Permission) {
            return other.requestCode == this.requestCode
        }
        return false
    }

    override fun hashCode(): Int {
        var result = requestCode
        result = 31 * result + isGranted.hashCode()
        result = 31 * result + hasAskedPermission.hashCode()
        return result
    }
}