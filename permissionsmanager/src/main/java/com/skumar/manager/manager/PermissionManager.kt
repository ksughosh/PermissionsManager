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

package com.skumar.manager.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import com.skumar.manager.data.PermissionResponse
import com.skumar.manager.manager.data.PermissionContext
import com.skumar.manager.manager.data.PermissionData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.lang.ref.WeakReference

interface PermissionManager {
    val allManifestPermissions: Array<String>
    fun requestPermission(vararg permissions: String): Single<PermissionResponse>
    fun checkPermission(vararg permissions: String): Observable<PermissionData>
    fun requestManifestPermission(): Single<PermissionResponse> = requestPermission(*allManifestPermissions)

    class Builder {
        private var application: WeakReference<Application>? = null
        private var enableStore = false
        private var activity: WeakReference<Activity>? = null
        fun setApplication(application: Application): Builder {
            this.application = WeakReference(application)
            return this
        }

        fun setActivity(activity: Activity): Builder {
            this.activity = WeakReference(activity)
            return this
        }

        fun setEnablesStore(enableStore: Boolean): Builder {
            this.enableStore = enableStore
            return this
        }

        fun build(): PermissionManager {
            val context = when {
                application != null -> PermissionContext.ApplicationContext(application!!)
                activity != null -> PermissionContext.ActivityContext(activity!!)
                else -> throw RuntimeException("context is not provided during initialization")
            }
            val permissionStore = context.get()?.let {
                val name = it.packageName
                PermissionStore(sharedPreferences = it.getSharedPreferences(name, Context.MODE_PRIVATE))
            }
            return PermissionManagerImpl(context, permissionStore)
        }
    }
}
