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


