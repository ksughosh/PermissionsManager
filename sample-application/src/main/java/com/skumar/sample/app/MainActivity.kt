package com.skumar.sample.app

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skumar.manager.data.PermissionResponse.*
import com.skumar.manager.manager.PermissionManager

class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionManager = PermissionManager.Builder()
                .setApplication(application)
                .setEnablesStore(true)
                .build()

        permissionManager.requestPermission(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe(
                {
                    when (it) {
                        is Granted -> Log.d(TAG, "permission granted ${it.permissions}")
                        is Denied -> Log.d(TAG, "permission denied ${it.permissions}")
                        is DeniedForever -> Log.d(TAG, "permission denied forever ${it.permissions}")
                        is Mixed -> Log.d(TAG, "permission Mixed ${it.permissions} " +
                                "with responses ${it.responses.map { it::class.java.simpleName }}")
                    }
                    grantedPermissionCheck()
                }, { Log.e(TAG, "Error", it) }
        )
    }

    private fun grantedPermissionCheck() {
        val permissionGranted = permissionManager.checkPermission(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        )
        permissionGranted.forEach {
            Log.d(TAG, "permission checked ${it.permission}, " +
                    "granted:${it.isGranted}, " +
                    "isInStore: ${it.isInStore}"
            )
        }

    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
