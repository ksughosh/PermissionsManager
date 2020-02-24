package com.skumar.permissionsmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * @author s.kumar on 26/09/2017.
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // uncomment the following lines and comment
        // the fragment transaction to get the
        // permission request from activity

//        val manager = PermissionManagerImpl(this)
//        val permissions = Permission()
//        permissions.permissionArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.BODY_SENSORS, Manifest.permission.CAMERA)
//        manager.requestPermission(permissions).subscribe {perm ->
//            Log.d("PERMISSION", "Granted: "
//                    + perm.isGranted + "\nasked: " + perm.hasAskedPermission
//                    + "\nnever: " + perm.neverAskPermission)
//        }

        val testFragment = TestFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, testFragment)
                .commit()
    }
}
