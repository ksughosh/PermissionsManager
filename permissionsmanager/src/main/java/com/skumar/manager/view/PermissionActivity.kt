package com.skumar.manager.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skumar.manager.data.Permission
import com.skumar.manager.viewModel.PermissionViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class PermissionActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
    private lateinit var viewModel: PermissionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = lastCustomNonConfigurationInstance as? PermissionViewModel
                ?: PermissionViewModelProvider.get()

        val permission = intent.getParcelableExtra<Permission>(keyPermissions) ?: return

        disposables += viewModel.requestPermission(activity = this, permission = permission)
                .subscribe({
                    viewModel.permissionResponse(activity = this, permissionRequest = it)
                }) { viewModel.error(it) }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (!viewModel.onPermissionResult(requestCode, permissions, this, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        finish()
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }

    override fun onRetainCustomNonConfigurationInstance(): Any = viewModel

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    internal companion object {
        const val keyPermissions = "keyPermissions"
    }
}


