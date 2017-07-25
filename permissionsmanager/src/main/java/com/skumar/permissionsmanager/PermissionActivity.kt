package com.skumar.permissionsmanager

import android.annotation.TargetApi
import android.os.Build
import android.support.v7.app.AppCompatActivity

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
open class PermissionActivity: AppCompatActivity(), PermissionView {

    override var currentPermission: Permission?
        get() = permission
        set(value) {
            permission = value
        }

    override var callback: ((permission: Permission) -> Unit)?
        get() = callBack
        set(value) {
            callBack = value
        }

    override var isAskingForPermission: Boolean
        get() = askingPermission
        set(value) {
            askingPermission = value
        }

    override fun requestPermission(permissions: Array<String>, permissionS_REQUEST: Int) {
        requestPermissions(permissions, permissionS_REQUEST)
    }

    var askingPermission = false
    var permission: Permission? = null
    var callBack: ((permission: Permission) -> Unit)? = null



    override fun onStop() {
        isAskingForPermission = false
        super.onStop()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val showRationale = permissions.all { shouldShowRequestPermissionRationale(it) }
        onPermissionResult(requestCode, grantResults, showRationale)
    }
}