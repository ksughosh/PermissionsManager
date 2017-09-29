package com.skumar.permissionsmanager

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment

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
@TargetApi(Build.VERSION_CODES.M)
class PermissionFragment : Fragment() {
    private lateinit var permission: Permission
    var permissionCallback: ((permission: Permission) -> Unit)? = null

    companion object {
        val TAG = "PermissionFragment"
        val PERMISSION_REQUEST = 0x2332
        fun newInstance(permission: Permission, callback: (permission: Permission) -> Unit): PermissionFragment {
            val fragment = PermissionFragment()
            fragment.init(permission, callback)
            return fragment
        }
    }

    private fun init(permission: Permission, callback: (permission: Permission) -> Unit) {
        this.permission = permission
        this.permissionCallback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arrayOfPermissions = mutableListOf<String>()
        permission.permissionArray.forEach {
            if (activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                arrayOfPermissions.add(it)
            }
        }

        if (!arrayOfPermissions.isEmpty()) {
            requestPermissions(arrayOfPermissions.toTypedArray(), PERMISSION_REQUEST)
        } else {
            permission.hasAskedPermission = false
            permission.neverAskPermission = permission.permissionArray.all { shouldShowRequestPermissionRationale(it) }
            permission.isGranted = false
            permissionCallback?.invoke(permission)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permission.isGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        permission.hasAskedPermission = true
        permissionCallback?.invoke(permission)
    }

    @SuppressLint("CheckResult")
    fun isRevoked(permission: Permission): Boolean {
        return permission.getPermissionString().all { activity.packageManager.isPermissionRevokedByPolicy(it, activity.packageName) }
    }
}
