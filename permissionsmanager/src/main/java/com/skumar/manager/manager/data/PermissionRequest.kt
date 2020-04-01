package com.skumar.manager.manager.data

import com.skumar.manager.data.Permission

internal sealed class PermissionRequest {
    internal abstract val permission: Permission

    internal class AskPermission(override val permission: Permission) : PermissionRequest()
    internal class AlreadyGranted(override val permission: Permission) : PermissionRequest()
    internal class DeniedForever(override val permission: Permission) : PermissionRequest()
    internal class InvalidPermission(override val permission: Permission) : PermissionRequest()
}