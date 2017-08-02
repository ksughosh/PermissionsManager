package com.skumar.permissionsmanager

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by s.kumar on 02/08/2017.
 */
inline val Context.defaultSharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)!!