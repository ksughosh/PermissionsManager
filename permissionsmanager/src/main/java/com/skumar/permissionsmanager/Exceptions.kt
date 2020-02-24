package com.skumar.permissionsmanager

sealed class PermissionException {
    class InvalidPermissions(val permissions: Array<String>) : Exception("Permission construction is invalid")
    object InvalidRequest: Exception("error with permission request")
    object UnrecoverableError: Exception("unrecoverable error")
}
