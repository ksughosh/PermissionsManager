package com.skumar.permissionsmanager

data class Permission(
        val permissionArray: Array<out String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Permission) return false
        return permissionArray.contentEquals(other.permissionArray)
    }

    override fun hashCode(): Int = permissionArray.contentHashCode()
}

internal sealed class PermissionRequest {
    internal abstract val permission: Permission
    internal data class AskPermission(override val permission: Permission): PermissionRequest()
    internal data class DeniedForever(override val permission: Permission): PermissionRequest()
    internal data class InvalidPermission(override val permission: Permission): PermissionRequest()
}

sealed class PermissionResponse {
    abstract val permission: Permission

    data class Granted(override val permission: Permission) : PermissionResponse()
    data class Denied(override val permission: Permission) : PermissionResponse()
    data class DeniedForever(override val permission: Permission) : PermissionResponse()
}
