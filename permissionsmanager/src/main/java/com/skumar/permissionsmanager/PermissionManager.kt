package com.skumar.permissionsmanager

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.support.annotation.CallSuper
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.loop.toolkit.kotlin.Utils.Exception.IllegalClassException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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
open class PermissionManager(var activity: FragmentActivity, private val enableStore: Boolean = false) {

    constructor(fragment: Fragment) : this(fragment.activity)

    var permissionStore: SharedPreferences? =
            if (enableStore){
                activity.defaultSharedPreferences
            } else {
                null
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

    val allPermissionsFromManifest: Observable<Permission>
        get() {
            val packageManager = activity.packageManager
            val packageName = activity.packageName
            if (packageManager == null || packageName == null) throw IllegalClassException (
                    "Context is null or invalid package name")
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val allPermissions = Permission()
            allPermissions.permissionArray = packageInfo.requestedPermissions
            return askPermission(allPermissions)
        }


    companion object {

        val REQUEST_PERMISSION_CODE = 123

        const val REQUEST_CAMERA_PERMISSION = 0
        const val REQUEST_LOCATION_PERMISSION = 1
        const val REQUEST_MICROPHONE_PERMISSION = 2
        const val REQUEST_CALENDAR_PERMISSION = 3
        const val REQUEST_CONTACTS_PERMISSION = 4
        const val REQUEST_STORAGE_PERMISSION = 5
        const val REQUEST_PHONE_PERMISSION = 6
        const val REQUEST_BODY_SENSOR_PERMISSION = 7
        const val REQUEST_SMS_PERMISSION = 8
        const val REQUEST_PERMISSIONS_FROM_MANIFEST = 9

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(REQUEST_CAMERA_PERMISSION.toLong(),
                REQUEST_LOCATION_PERMISSION.toLong(),
                REQUEST_MICROPHONE_PERMISSION.toLong(),
                REQUEST_CALENDAR_PERMISSION.toLong(),
                REQUEST_CONTACTS_PERMISSION.toLong(),
                REQUEST_STORAGE_PERMISSION.toLong(),
                REQUEST_PHONE_PERMISSION.toLong(),
                REQUEST_BODY_SENSOR_PERMISSION.toLong(),
                REQUEST_SMS_PERMISSION.toLong(),
                REQUEST_PERMISSIONS_FROM_MANIFEST.toLong())
        annotation class RequestPermission
    }

    @CallSuper protected fun askPermission(permissions: Permission): Observable<Permission> {
        return Observable.create<Permission> { emitter ->
            assertMainThread()
            if (permissions.getPermissionString().all { activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) {
                permissions.isGranted = true
                permissions.hasAskedPermission = false
                emitter.onNext(permissions)

            } else {
                val permissionFragment = PermissionFragment.newInstance(permissions)
                permissionFragment.permissionCallback = { permission ->
                    emitter.onNext(permission)
                    emitter.onComplete()
                }

                activity.supportFragmentManager
                        .beginTransaction()
                        .add(permissionFragment, PermissionFragment.TAG)
                        .commitAllowingStateLoss()
            }
            permissions.preferences = permissionStore
        }
    }

    private fun assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("Cannot request permissions off the main thread.")
        }
    }

    fun requestPermission(permission: Permission): Observable<Permission> {
        return askPermission(permission)
    }


    fun getPermissionsFor(@RequestPermission permission: Int): Observable<Permission> {
        when (permission) {
            REQUEST_CAMERA_PERMISSION -> return cameraPermission
            REQUEST_LOCATION_PERMISSION -> return locationPermission
            REQUEST_MICROPHONE_PERMISSION -> return microphonePermission
            REQUEST_CALENDAR_PERMISSION -> return calendarPermission
            REQUEST_CONTACTS_PERMISSION -> return contactsPermission
            REQUEST_STORAGE_PERMISSION -> return storagePermission
            REQUEST_PHONE_PERMISSION -> return phonePermission
            REQUEST_BODY_SENSOR_PERMISSION -> return bodySensorPermission
            REQUEST_SMS_PERMISSION -> return smsPermission
            REQUEST_PERMISSIONS_FROM_MANIFEST -> return allPermissionsFromManifest
        }
        return Observable.empty()
    }
}
