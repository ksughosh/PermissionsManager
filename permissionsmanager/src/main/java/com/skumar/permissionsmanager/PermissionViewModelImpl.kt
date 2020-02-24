package com.skumar.permissionsmanager

import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import com.skumar.permissionsmanager.PermissionException.InvalidPermissions
import com.skumar.permissionsmanager.PermissionException.InvalidRequest
import com.skumar.permissionsmanager.PermissionRequest.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.SingleSubject

internal class PermissionViewModelImpl private constructor(
        private val permission: Permission
) : PermissionViewModel, PermissionResponseSingle {
    private val permissionResponseSubject = SingleSubject.create<PermissionResponse>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun requestPermission(permissionFragment: PermissionFragment): Single<PermissionRequest> = Single.create {
        val activity = permissionFragment.activity ?: return@create
        val permissionArray = permission.permissionArray
        val permissions = permissionArray.filter { permission ->
            activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
        }
        val rationale = permissionArray.all(activity::shouldShowRequestPermissionRationale)
        val revoked = permissionArray.any { permission ->
            activity.packageManager.isPermissionRevokedByPolicy(permission, activity.packageName)
        }
        if (!it.isDisposed) {
            if (permissions.isEmpty()) {
                it.onSuccess(InvalidPermission(permission))
            } else if (!rationale) {
                it.onSuccess(DeniedForever(permission))
            } else if (revoked) {
                it.onSuccess(AskPermission(permission))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun permissionResponse(
            fragment: PermissionFragment,
            permissionRequest: PermissionRequest
    ) {
        val permission = permissionRequest.permission
        when (permissionRequest) {
            is AskPermission -> fragment.requestPermissions(permission.permissionArray, PERMISSION_REQUEST)
            is DeniedForever -> permissionResponseSubject.onSuccess(PermissionResponse.DeniedForever(permission))
            is InvalidPermission -> permissionResponseSubject.onError(Throwable("Invalid permission request"))
        }
    }

    override fun permissionResponse(): Single<PermissionResponse> = permissionResponseSubject.hide()

    override fun onPermissionResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ): Boolean {
        return if (requestCode != PERMISSION_REQUEST) {
            permissionResponseSubject.onError(InvalidRequest).let { false }
        } else if (!permission.permissionArray.contentEquals(permissions)) {
            permissionResponseSubject.onError(InvalidPermissions(permissions)).let { false }
        } else {
            val isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (isGranted) {
                permissionResponseSubject.onSuccess(PermissionResponse.Granted(permission))
            } else {
                permissionResponseSubject.onSuccess(PermissionResponse.Denied(permission))
            }.let { true }
        }
    }

    override fun error(error: Throwable) {
        permissionResponseSubject.onError(error)
    }


    companion object {
        const val PERMISSION_REQUEST = 0x3232

        fun create(permission: Permission): Pair<PermissionViewModel, PermissionResponseSingle> {
            val fragmentViewModel = PermissionViewModelImpl(permission)
            val viewModel: PermissionViewModel = fragmentViewModel
            val permissionResponse: PermissionResponseSingle = fragmentViewModel
            return viewModel to permissionResponse
        }
    }
}
