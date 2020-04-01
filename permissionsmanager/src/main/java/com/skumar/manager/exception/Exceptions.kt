package com.skumar.manager.exception

sealed class PermissionException {
    class InvalidPermissions(val permissions: Array<out String>) : Exception("Permission construction is invalid")
    object InvalidRequest: Exception("error with permission request")
    object UnrecoverableError: Exception("unrecoverable error")
}
