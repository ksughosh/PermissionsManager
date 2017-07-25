package com.skumar.permissionsmanager

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.preference.PreferenceManager
import android.support.annotation.CallSuper
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.SparseArray
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalStateException

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
open class PermissionManager(val permissionView: PermissionView, private val enableStore: Boolean = false){
    val permissionStore: SharedPreferences?
        get() {
            if (enableStore){
                if (permissionView is PermissionActivity) {
                    return PreferenceManager.getDefaultSharedPreferences(permissionView)
                } else if (permissionView is PermissionFragment) {
                    return PreferenceManager.getDefaultSharedPreferences(permissionView.context)
                }
            }
            return null
        }

    val mainThread = AndroidSchedulers.mainThread()
    val newThread = Schedulers.newThread()

    val cameraPermission : Observable<Permission>
        get() = askPermission(Permission.CAMERA)

    val locationPermission: Observable<Permission>
        get() = askPermission(Permission.LOCATION)

    val microphonePermission: Observable<Permission>
        get() = askPermission(Permission.MICROPHONE)

    val calendarPermission: Observable<Permission>
        get() = askPermission(Permission.CALENDAR)

    val contactsPermission: Observable<Permission>
        get() = askPermission(Permission.CONTACTS)

    val storagePermission: Observable<Permission>
        get() = askPermission(Permission.STORAGE)

    val phonePermission: Observable<Permission>
        get() = askPermission(Permission.PHONE)

    val bodySensorPermission: Observable<Permission>
        get() = askPermission(Permission.BODY_SENSOR)

    val smsPermission: Observable<Permission>
        get() = askPermission(Permission.SMS)

    private val DEAD_FRAGMENT = Fragment()
    private val DEAD_ACTIVITY = PermissionActivity()
    companion object {

        const val REQUEST_CAMERA_PERMISSION = 0
        const val REQUEST_LOCATION_PERMISSION = 1
        const val REQUEST_MICROPHONE_PERMISSION = 2
        const val REQUEST_CALENDAR_PERMISSION = 3
        const val REQUEST_CONTACTS_PERMISSION = 4
        const val REQUEST_STORAGE_PERMISSION = 5
        const val REQUEST_PHONE_PERMISSION = 6
        const val REQUEST_BODY_SENSOR_PERMISSION = 7
        const val REQUEST_SMS_PERMISSION = 8

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(REQUEST_CAMERA_PERMISSION.toLong(),
                REQUEST_LOCATION_PERMISSION.toLong(),
                REQUEST_MICROPHONE_PERMISSION.toLong(),
                REQUEST_CALENDAR_PERMISSION.toLong(),
                REQUEST_CONTACTS_PERMISSION.toLong(),
                REQUEST_STORAGE_PERMISSION.toLong(),
                REQUEST_PHONE_PERMISSION.toLong(),
                REQUEST_BODY_SENSOR_PERMISSION.toLong(),
                REQUEST_SMS_PERMISSION.toLong())
        annotation class RequestPermission
    }

    @Volatile private var observerMap = SparseArray<Observable<Permission>>()
    var neverAskPermission: Boolean = false

    @CallSuper protected fun askPermission(permissions: Permission): Observable<Permission> {
        if (isPermissionGranted(permissions.getPermissionString())) {
            permissions.isGranted = true
            return Observable.just(permissions)
        }
        if (permissionView.isAskingForPermission) {
            return Observable.empty()
        }

        val observer = Observable.create<Permission> { e ->
            assertMainThread()
            if (permissionView.isAskingForPermission) {
                throw IllegalStateException("Permission is already being requested!")
            }
            permissions.preferences = permissionStore
            permissionView.checkPermission(permissions, { permission ->
                neverAskPermission = !permission.neverAskPermission
                e.onNext(permission)
            })

        }.subscribeOn(mainThread)
        observerMap.put(permissions.hashCode(), observer)
        return observer
    }

    private fun isPermissionGranted(permission: Array<String>): Boolean {
        if (permissionView is PermissionActivity) {
            return permission.all { ContextCompat.checkSelfPermission(permissionView, it) == PackageManager.PERMISSION_GRANTED }
        }

        return false
    }

    private fun assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException(
                    "Cannot request permissions off the main thread.")
        }
    }

    fun requestPermission(permission: Permission): Observable<Permission> {
        return askPermission(permission)
    }

    fun getPermissionsFor(@RequestPermission permission: Int): Observable<Permission> {
        when (permission) {
            REQUEST_CAMERA_PERMISSION -> return askPermission(Permission.CAMERA)
            REQUEST_LOCATION_PERMISSION -> return askPermission(Permission.LOCATION)
            REQUEST_MICROPHONE_PERMISSION -> return askPermission(Permission.MICROPHONE)
            REQUEST_CALENDAR_PERMISSION -> return askPermission(Permission.CALENDAR)
            REQUEST_CONTACTS_PERMISSION -> return askPermission(Permission.CONTACTS)
            REQUEST_STORAGE_PERMISSION -> return askPermission(Permission.STORAGE)
            REQUEST_PHONE_PERMISSION -> return askPermission(Permission.PHONE)
            REQUEST_BODY_SENSOR_PERMISSION -> return askPermission(Permission.BODY_SENSOR)
            REQUEST_SMS_PERMISSION -> return askPermission(Permission.SMS)
        }
        return Observable.empty()
    }
}