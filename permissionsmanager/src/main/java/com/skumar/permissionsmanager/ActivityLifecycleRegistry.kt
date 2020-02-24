package com.skumar.permissionsmanager

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ActivityLifecycleRegistry: Application.ActivityLifecycleCallbacks {
    var activity: Activity? = null
    override fun onActivityPaused(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {}
    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityDestroyed(activity: Activity?) {
        this.activity = null
    }
    override fun onActivitySaveInstanceState(activity: Activity?, p1: Bundle?) {}
    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivityCreated(activity: Activity?, p1: Bundle?) {
        this.activity = activity
    }

}