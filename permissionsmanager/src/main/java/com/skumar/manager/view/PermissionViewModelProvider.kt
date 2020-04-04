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

import android.content.Context
import android.content.Intent
import com.skumar.manager.data.Permission
import com.skumar.manager.manager.PermissionResponseSingle
import com.skumar.manager.viewModel.PermissionViewModel
import com.skumar.manager.viewModel.PermissionViewModelImpl
import java.lang.ref.WeakReference

internal object PermissionViewModelProvider {
    private lateinit var viewModelReference: WeakReference<PermissionViewModel>

    fun createIntentWithViewModel(
            context: Context,
            permission: Permission,
            vararg flags: Int
    ): Pair<Intent, PermissionResponseSingle> {
        val viewModel = PermissionViewModelImpl()
        val intent = Intent(context, PermissionActivity::class.java)
        intent.putExtra(PermissionActivity.keyPermissions, permission)
        flags.forEach { intent.addFlags(it) }
        viewModelReference = WeakReference(viewModel)
        return intent to viewModel
    }

    fun get(): PermissionViewModel = viewModelReference.get()!!
}
