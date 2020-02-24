package com.skumar.permissionsmanager

import io.reactivex.rxjava3.core.Single

interface PermissionManager {
    val allPermissionsFromManifest: Single<PermissionResponse>
    fun requestPermission(vararg permissions: String): Single<PermissionResponse>
}
