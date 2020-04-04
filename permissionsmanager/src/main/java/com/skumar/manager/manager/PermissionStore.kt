/*
 * @author Sughosh on 04/03/2020.
 *
 * Copyright (C)  04/03/2020 Sughosh Krishna Kumar
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE SUGHOSH KRISHNA KUMAR BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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
