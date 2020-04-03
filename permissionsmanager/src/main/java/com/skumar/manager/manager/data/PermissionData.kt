package com.skumar.manager.manager.data

import com.skumar.manager.data.PermissionResponse

data class PermissionData constructor(
        val permission: String,
        val isGranted: Boolean,
        val isInStoreValue: PermissionResponse?
) {
    var isStoreEnabled: Boolean = isInStoreValue != null
        internal set
}
