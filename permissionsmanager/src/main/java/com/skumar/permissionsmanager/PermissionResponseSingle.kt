package com.skumar.permissionsmanager

import io.reactivex.rxjava3.core.Single

interface PermissionResponseSingle {
    fun permissionResponse(): Single<PermissionResponse>
}
