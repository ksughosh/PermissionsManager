package com.skumar.manager.manager

import android.content.SharedPreferences

class PermissionStore(private val sharedPreferences: SharedPreferences) {

    fun insertGrantedPermission(vararg permissions: String): Boolean =
            insertOrUpdate(grantedPermissionKey, *permissions)

    fun insertDeniedPermission(vararg permissions: String): Boolean =
            insertOrUpdate(deniedPermissionsKey, *permissions)

    fun insertDeniedForeverPermission(vararg permissions: String): Boolean =
            insertOrUpdate(deniedForeverPermissionKey, *permissions)

    private fun insertOrUpdate(key: String, vararg permissions: String): Boolean {
        val permissionArray = get(key, permissions)
        if (permissionArray.isEmpty()) {
            return false
        }
        val valueToInsert = if (sharedPreferences.contains(key)) {
            val currentPermissionSet = sharedPreferences.getString(key, "")
            val reduce = permissionArray.reduce { acc, s -> ":$acc:$s" }
            "$currentPermissionSet$reduce"
        } else {
            permissionArray.reduce { acc, s -> ":$acc:$s" }
        }
        sharedPreferences.edit()
                .putString(key, valueToInsert)
                .apply()
        return true
    }

    fun hasStoredPermission(permission: String): Boolean {
        val permissionSet = sharedPreferences.getString(grantedPermissionKey, "") ?: return false
        return permissionSet.contains(permission)
    }

    private fun get(key: String, permissionArray: Array<out String>): Array<out String> {
        if (sharedPreferences.contains(key)) {
            val permissions = sharedPreferences.getString(grantedPermissionKey, "")
                    .takeIf { !it.isNullOrBlank() } ?: return permissionArray
            return permissionArray.filter { !permissions.contains(it) }.toTypedArray()
        }
        return arrayOf()
    }

    private companion object {
        const val grantedPermissionKey = "grantedPermissionKey"
        const val deniedPermissionsKey = "deniedPermissionsKey"
        const val deniedForeverPermissionKey = "deniedForeverPermissionKey"
    }
}
