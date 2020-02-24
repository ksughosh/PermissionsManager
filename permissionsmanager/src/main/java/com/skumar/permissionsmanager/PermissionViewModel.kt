package com.skumar.permissionsmanager

import io.reactivex.rxjava3.core.Single


internal interface PermissionViewModel {
    fun requestPermission(permissionFragment: PermissionFragment): Single<PermissionRequest>
    fun permissionResponse(fragment: PermissionFragment, permissionRequest: PermissionRequest)
    fun onPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Boolean
    fun error(error: Throwable)
}
