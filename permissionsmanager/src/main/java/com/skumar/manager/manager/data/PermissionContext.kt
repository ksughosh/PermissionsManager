package com.skumar.manager.manager.data

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import java.lang.ref.WeakReference

internal sealed class PermissionContext<T : Context> {
    protected abstract val reference: WeakReference<T>
    fun get(): T? = reference.get()
    val packageName: String?
        get() = get()?.packageName
    val packageManager: PackageManager?
        get() = get()?.packageManager

    class ApplicationContext(override val reference: WeakReference<Application>) : PermissionContext<Application>()
    class ActivityContext(override val reference: WeakReference<Activity>) : PermissionContext<Activity>()
}