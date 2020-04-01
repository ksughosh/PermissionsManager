package com.skumar.manager.manager

import com.skumar.manager.data.PermissionResponse
import io.reactivex.rxjava3.core.Single

interface PermissionResponseSingle {
    fun permissionResponse(): Single<PermissionResponse>
}
