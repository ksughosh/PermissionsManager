package com.skumar.manager.viewModel

import android.app.Activity
import com.skumar.manager.data.Permission
import com.skumar.manager.manager.data.PermissionRequest
import io.reactivex.rxjava3.core.Single


internal interface PermissionViewModel {
    fun requestPermission(activity: Activity, permission: Permission): Single<PermissionRequest>
    fun permissionResponse(activity: Activity, permissionRequest: PermissionRequest)

    fun onPermissionResult(
            requestCode: Int,
            permissionArray: Array<String>,
            activity: Activity,
            grantResults: IntArray
    ): Boolean

    fun error(error: Throwable)
}
