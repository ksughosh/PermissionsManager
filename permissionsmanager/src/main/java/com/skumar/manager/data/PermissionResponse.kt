package com.skumar.manager.data

sealed class PermissionResponse {
    abstract val permissions: Array<out String>

    data class Granted internal constructor(
            private val permission: Permission
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }

    data class Denied internal constructor(
            private val permission: Permission
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }

    data class DeniedForever internal constructor(
            private val permission: Permission
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }

    class Mixed internal constructor(
            private val permission: Permission,
            vararg val responses: PermissionResponse
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }
}
