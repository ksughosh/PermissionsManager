package com.skumar.permissionsmanager

import android.content.pm.PackageManager

/**
 * @author s.kumar on 18/07/2017.
 *
 * Copyright (C)  18/07/2017 Sughosh Krishna Kumar
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE SUGHOSH KRISHNA KUMAR BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
interface PermissionView {
    var isAskingForPermission: Boolean
    var currentPermission: Permission?
    var callback: ((permission: Permission) -> Unit)?

    fun checkPermission(permissions: Array<String>, callback: (permission: Permission) -> Unit) {
        currentPermission = Permission.fromPermissions(permissions)
        isAskingForPermission = true
        currentPermission?.hasAskedPermission = isAskingForPermission
        requestPermission(permissions, PERMISSIONS_REQUEST)
        this.callback = callback
    }

    fun  checkPermission(permissions: Permission, callback: (permission: Permission) -> Unit) {
        isAskingForPermission = true
        permissions.hasAskedPermission = isAskingForPermission
        currentPermission = permissions
        requestPermission(permissions.getPermissionString(), PERMISSIONS_REQUEST)
        this.callback = callback
    }

    fun onPermissionResult(requestCode: Int, grantResults: IntArray, shouldShowRationale: Boolean) {
        if (requestCode != PERMISSIONS_REQUEST) {
            return
        }
        val curPermission = currentPermission
        if (curPermission != null) {
            curPermission.isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            curPermission.neverAskPermission = shouldShowRationale
            curPermission.hasAskedPermission = true
            val cb = callback
            if (cb != null) {
                cb(curPermission)
            }
        }
    }

    fun requestPermission(permissions: Array<String>, permissionS_REQUEST: Int)

    companion object {
        val PERMISSIONS_KEY = "permissions"
        val PERMISSIONS_REQUEST = 223
    }
}