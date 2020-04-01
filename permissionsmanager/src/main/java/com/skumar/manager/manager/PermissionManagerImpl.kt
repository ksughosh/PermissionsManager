@file:Suppress("DEPRECATION")

package com.skumar.manager.manager

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.skumar.manager.data.Permission
import com.skumar.manager.data.PermissionResponse
import com.skumar.manager.data.PermissionResponse.*
import com.skumar.manager.exception.IllegalClassException
import com.skumar.manager.manager.data.PermissionContext
import com.skumar.manager.manager.data.PermissionContext.ActivityContext
import com.skumar.manager.manager.data.PermissionContext.ApplicationContext
import com.skumar.manager.view.PermissionViewModelProvider
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

@Suppress("DEPRECATION")
@TargetApi(Build.VERSION_CODES.M)
internal class PermissionManagerImpl constructor(
        private val permissionContext: PermissionContext<*>,
        private val enableStore: Boolean = false
) : PermissionManager {

    private val permissionStore: SharedPreferences? =
            permissionContext.get()?.run {
                getSharedPreferences(packageName, Context.MODE_PRIVATE).takeIf { enableStore }
            }

    override val allPermissionsFromManifest: Single<PermissionResponse>
        get() {
            val packageManager = permissionContext.packageManager
            val packageName = permissionContext.packageName
            if (packageManager == null || packageName == null)
                throw IllegalClassException("Context is null or invalid package name")
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            return askPermission(*packageInfo.requestedPermissions)
        }

    private fun askPermission(vararg permissions: String): Single<PermissionResponse> {
        val permission = Permission(permissions)
        return Single.fromCallable {
            assertMainThread()
            val context = permissionContext.get() ?: throw Exception("context is null")
            permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }
                .filter { it }
                .map<PermissionResponse> { Granted(permission) }
                .switchIfEmpty(startOrAttach(permission))
                .doOnSuccess { permissionStore?.insertOrUpdate(it) }
    }

    private fun startOrAttach(permission: Permission): Single<PermissionResponse> {
        val (intent, response) = when (permissionContext) {
            is ApplicationContext -> permissionContext.get()?.run {
                PermissionViewModelProvider.createIntentWithViewModel(
                        this,
                        permission,
                        Intent.FLAG_ACTIVITY_NEW_TASK
                )
            }
            is ActivityContext -> permissionContext.get()?.run {
                PermissionViewModelProvider.createIntentWithViewModel(
                        this,
                        permission
                )
            }
        } ?: return Single.error(Throwable("failed to initialize with context, is null"))

        permissionContext.get()?.startActivity(intent)
        return response.permissionResponse()
    }

    private fun assertMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw RuntimeException("Cannot request permissions outside the main thread.")
        }
    }

    override fun requestPermission(vararg permissions: String): Single<PermissionResponse> = askPermission(*permissions)

    private fun SharedPreferences.insertOrUpdate(permissionResponse: PermissionResponse) {
        when (permissionResponse) {
            is Granted -> insertOrUpdate(
                    grantedPermissions,
                    permissionResponse.permissions
            )
            is Denied -> insertOrUpdate(
                    deniedPermissions,
                    permissionResponse.permissions
            )
            is DeniedForever -> insertOrUpdate(
                    deniedForeverPermission,
                    permissionResponse.permissions
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
