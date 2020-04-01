@file:Suppress("DEPRECATION")

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
