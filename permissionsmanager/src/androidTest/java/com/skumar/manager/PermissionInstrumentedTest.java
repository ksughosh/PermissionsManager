package com.skumar.manager;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.skumar.manager.data.Permission;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PermissionInstrumentedTest {
    private Permission permission;
    private Context appContext;

    @Before
    public void setup() {
        permission = new Permission(0);
        Context appContext = InstrumentationRegistry.getTargetContext();
        permission.setPreferences(PreferenceManager.getDefaultSharedPreferences(appContext));
    }

    @Test
    public void test_PermissionStore() throws Exception {
        assertNotNull(permission.getPreferences());
    }

    @Test
    public void test_PermissionGrant() throws Exception {
        assertFalse(permission.isGranted());
        permission.setHasAskedPermission(true);
        permission.setNeverAskPermission(true);
        assertNotNull(permission.getPreferences());
        boolean hasAsked = permission.getPreferences().getBoolean(Permission.Companion.getASK_PERMISSION(), false);
        boolean neverAsk = permission.getPreferences().getBoolean(permission.getString(), false);
        assertEquals(permission.getHasAskedPermission(), hasAsked);
        assertEquals(permission.getNeverAskPermission(), neverAsk);
        permission.setGranted(true);
        assertTrue(permission.isGranted());
    }
}
