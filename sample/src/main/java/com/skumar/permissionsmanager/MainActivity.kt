package com.skumar.permissionsmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : PermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = PermissionManager(this)
        manager.cameraPermission.subscribe { perm ->
            Log.d("PERMISSION", "Granted: "
                    + perm.isGranted + "\nasked: " + perm.hasAskedPermission
                    + "\nnever: " + perm.neverAskPermission)
        }
    }
}
