package com.skumar.permissionsmanager

import android.annotation.TargetApi
import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.support.annotation.CallSuper
import com.skumar.permissionsmanager.PermissionException.UnrecoverableError
import com.skumar.permissionsmanager.PermissionResponse.*
import io.reactivex.rxjava3.core.Single

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
open class PermissionManagerImpl(
        private val application: Application,
        private val enableStore: Boolean = false
) : PermissionManager {
    private val activityLifecycleRegistry = ActivityLifecycleRegistry()

    private val permissionStore: SharedPreferences? = application
            .defaultSharedPreferences.takeIf { enableStore }

    init {
        application.registerActivityLifecycleCallbacks(activityLifecycleRegistry)
    }


    override val allPermissionsFromManifest: Single<PermissionResponse>
        get() {
            val packageManager = application.packageManager
            val packageName = application.packageName
            if (packageManager == null || packageName == null) throw IllegalClassException(
                    "Context is null or invalid package name")
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            return askPermission(*packageInfo.requestedPermissions)
        }

    @CallSuper
    protected fun askPermission(vararg permissions: String): Single<PermissionResponse> {
        return Single.fromCallable {
            assertMainThread()
            val activity = activityLifecycleRegistry.activity ?: return@fromCallable false
            permissions.all { activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        }
                .filter { it }
                .map<PermissionResponse> { Granted(Permission(permissions)) }
                .switchIfEmpty(Single.defer {
                    assertMainThread()
                    val activity = activityLifecycleRegistry.activity
                            ?: return@defer Single.error<PermissionResponse>(UnrecoverableError)
                    val (viewModel, responseSingle) = PermissionViewModelImpl.create(Permission(permissions))
                    val permissionFragment = PermissionFragment.create(viewModel)

                    activity.fragmentManager
                            .beginTransaction()
                            .add(permissionFragment, PermissionFragment.TAG)
                            .commitAllowingStateLoss()

                    responseSingle.permissionResponse()
                })
                .doOnSuccess { permissionStore?.insertOrUpdate(it) }
    }

    private fun assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("Cannot request permissions off the main thread.")
        }
    }

    override fun requestPermission(vararg permissions: String): Single<PermissionResponse> = askPermission(*permissions)

    private fun SharedPreferences.insertOrUpdate(permissionResponse: PermissionResponse) {
        when (permissionResponse) {
            is Granted          -> insertOrUpdate(
                    grantedPermissions,
                    permissionResponse.permission.permissionArray
            )
            is Denied           -> insertOrUpdate(
                    deniedPermissions,
                    permissionResponse.permission.permissionArray
            )
            is DeniedForever    -> insertOrUpdate(
                    deniedForeverPermission,
                    permissionResponse.permission.permissionArray
            )
        }
    }

    private fun SharedPreferences.insertOrUpdate(key: String, value: Array<out String>) {
        val valueToInsert = if (contains(key)) {
            val currentPermissionSet = getString(key, "")
            val reduce = value.reduce { acc, s -> ":$acc:$s" }
            "$currentPermissionSet$reduce"
        } else {
            value.reduce { acc, s -> ":$acc:$s" }
        }
        edit().putString(key, valueToInsert).apply()
    }

    companion object {
        private const val grantedPermissions = "grantedPermissions"
        private const val deniedPermissions = "deniedPermissions"
        private const val deniedForeverPermission = "deniedForeverPermission"
    }

}
