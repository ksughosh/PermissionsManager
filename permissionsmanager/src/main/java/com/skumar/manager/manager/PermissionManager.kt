package com.skumar.manager.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import com.skumar.manager.data.PermissionResponse
import com.skumar.manager.manager.data.PermissionContext
import com.skumar.manager.manager.data.PermissionData
import io.reactivex.rxjava3.core.Single
import java.lang.ref.WeakReference

interface PermissionManager {
    val allPermissionsFromManifest: Single<PermissionResponse>
    fun requestPermission(vararg permissions: String): Single<PermissionResponse>
    fun checkPermission(vararg permissions: String): Array<PermissionData>

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
