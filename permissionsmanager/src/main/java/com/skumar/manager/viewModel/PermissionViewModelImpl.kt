/*
 * @author Sughosh on 04/03/2020.
 *
 * Copyright (C)  04/03/2020 Sughosh Krishna Kumar
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE SUGHOSH KRISHNA KUMAR BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.skumar.manager.viewModel

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.skumar.manager.data.Permission
import com.skumar.manager.data.PermissionResponse
import com.skumar.manager.exception.PermissionException.UnrecoverableError
import com.skumar.manager.manager.PermissionResponseSingle
import com.skumar.manager.manager.data.PermissionRequest
import com.skumar.manager.manager.data.PermissionRequest.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.SingleSubject

internal class PermissionViewModelImpl : PermissionViewModel, PermissionResponseSingle {
    private val permissionResponseSubject = SingleSubject.create<PermissionResponse>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun requestPermission(activity: Activity, permission: Permission): Single<PermissionRequest> = Single.create {
        val permissionArray = permission.permissionArray
        val permissions = permissionArray.filter { permission ->
            activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
        }
        val revoked = permissionArray.any { permission ->
            activity.packageManager.isPermissionRevokedByPolicy(permission, activity.packageName)
        }
        val isGranted = permissions.isEmpty() && permissionArray.all { permission ->
            activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
        val filteredPermission = Permission(permissions.toTypedArray())
        if (!it.isDisposed) {
            when {
                permissions.isEmpty() -> it.onSuccess(InvalidPermission(filteredPermission))
                revoked -> it.onSuccess(DeniedForever(filteredPermission))
                isGranted -> it.onSuccess(AlreadyGranted(filteredPermission))
                else -> it.onSuccess(AskPermission(filteredPermission))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun permissionResponse(
            activity: Activity,
            permissionRequest: PermissionRequest
    ) {
        val permission = permissionRequest.permission
        when (permissionRequest) {
            is AskPermission -> ActivityCompat.requestPermissions(activity, permission.permissionArray, PERMISSION_REQUEST)
            is AlreadyGranted -> permissionResponseSubject.onSuccess(PermissionResponse.Granted(permission))
            is DeniedForever -> permissionResponseSubject.onSuccess(PermissionResponse.DeniedForever(permission))
            is InvalidPermission -> permissionResponseSubject.onError(Throwable("Invalid permission request"))
        }
    }

    override fun permissionResponse(): Single<PermissionResponse> = permissionResponseSubject.hide()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPermissionResult(
            requestCode: Int,
            permissionArray: Array<String>,
            activity: Activity,
            grantResults: IntArray
    ): Boolean {
        return if (requestCode != PERMISSION_REQUEST) {
            permissionResponseSubject.onError(UnrecoverableError).let { false }
        } else {
            val isHandled = processPermissions(activity, permissionArray, grantResults)
            (activity as? PermissionHost)?.complete()
            isHandled
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun processPermissions(
            activity: Activity,
            permissionArray: Array<String>,
            grantResults: IntArray
    ): Boolean {
        val grantedList = mutableListOf<String>()
        val deniedList = mutableListOf<String>()
        val deniedForeverList = mutableListOf<String>()
        grantResults.forEachIndexed { index, result ->
            if (result == PackageManager.PERMISSION_GRANTED) {
                grantedList.add(permissionArray[index])
            } else if (!activity.shouldShowRequestPermissionRationale(permissionArray[index])) {
                deniedForeverList.add(permissionArray[index])
            } else {
                deniedList.add(permissionArray[index])
            }
        }
        return if (deniedForeverList.isEmpty() && deniedList.isEmpty() && grantedList.isNotEmpty()) {
            permissionResponseSubject.onSuccess(PermissionResponse.Granted(
                    Permission(grantedList.toTypedArray()))
            )
            true
        } else if (deniedForeverList.isEmpty() && grantedList.isEmpty() && deniedList.isNotEmpty()) {
            permissionResponseSubject.onSuccess(PermissionResponse.Denied(
                    Permission(deniedList.toTypedArray()))
            )
            false
        } else if (deniedList.isEmpty() && grantedList.isEmpty() && deniedForeverList.isNotEmpty()) {
            permissionResponseSubject.onSuccess(PermissionResponse.DeniedForever(
                    Permission(deniedForeverList.toTypedArray()))
            )
            false
        } else {
            val allPermissions = setOf(
                    *grantedList.toTypedArray(),
                    *deniedForeverList.toTypedArray(),
                    *deniedList.toTypedArray()
            ).toTypedArray()

            val permissionList = mutableListOf<PermissionResponse>()
            if (grantedList.isNotEmpty()) {
                permissionList.add(PermissionResponse.Granted(Permission(grantedList.toTypedArray())))
            }
            if (deniedForeverList.isNotEmpty()) {
                permissionList.add(PermissionResponse.DeniedForever(Permission(deniedForeverList.toTypedArray())))
            }
            if (deniedList.isNotEmpty()) {
                permissionList.add(PermissionResponse.Denied(Permission(deniedList.toTypedArray())))

            }
            permissionResponseSubject.onSuccess(PermissionResponse.Mixed(
                    Permission(allPermissions),
                    *permissionList.toTypedArray()
            ))
            true
        }
    }

    override fun error(error: Throwable) {
        permissionResponseSubject.onError(error)
    }

    companion object {
        const val PERMISSION_REQUEST = 0x3232
    }
}
