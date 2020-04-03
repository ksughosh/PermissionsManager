package com.skumar.manager.manager

import android.content.SharedPreferences
import com.skumar.manager.data.Permission
import com.skumar.manager.data.PermissionResponse
import com.skumar.manager.data.PermissionResponse.*

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

    fun storedPermission(permissionString: String): PermissionResponse? {
        val permission = Permission(arrayOf(permissionString))
        return getStoredPermissions(grantedPermissionKey)?.let { Granted(permission) }
                ?: getStoredPermissions(deniedPermissionsKey)?.let { Denied(permission) }
                ?: getStoredPermissions(deniedForeverPermissionKey)?.let { DeniedForever(permission) }
    }


    private fun get(key: String, permissionArray: Array<out String>): Array<out String> {
        if (sharedPreferences.contains(key)) {
            val permissions = getStoredPermissions(key) ?: return permissionArray
            return permissionArray.filter { !permissions.contains(it) }.toTypedArray()
        }
        return arrayOf()
    }

    private fun getStoredPermissions(key: String): String? =
            sharedPreferences.getString(key, "").takeIf { !it.isNullOrEmpty() }

    private companion object {
        const val grantedPermissionKey = "grantedPermissionKey"
        const val deniedPermissionsKey = "deniedPermissionsKey"
        const val deniedForeverPermissionKey = "deniedForeverPermissionKey"
    }
}
