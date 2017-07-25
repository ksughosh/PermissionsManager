package com.skumar.permissionsmanager

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : PermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = PermissionManager(this)
        val permissions = Permission()
        permissions.permissionArray = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.BODY_SENSORS, Manifest.permission.CAMERA)
        manager.cameraPermission.subscribe {perm ->
            Log.d("PERMISSION", "Granted: "
                    + perm.isGranted + "\nasked: " + perm.hasAskedPermission
                    + "\nnever: " + perm.neverAskPermission)
        }
    }
}
