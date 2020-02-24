package com.skumar.permissionsmanager

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * Created by s.kumar on 02/08/2017.
 */
val Context.defaultSharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)