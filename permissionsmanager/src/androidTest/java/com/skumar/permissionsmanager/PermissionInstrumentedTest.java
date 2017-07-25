package com.skumar.permissionsmanager;

import android.Manifest;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static junit.framework.Assert.assertEquals;
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

    @Test
    public void test_PermissionManager() throws Exception {
        final PermissionView view = new PermissionFragment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PermissionManager manager = new PermissionManager(view, true);
                Permission permission = new Permission(10);
                String[] permissionArray = new String[]{Manifest.permission.ACCESS_CHECKIN_PROPERTIES, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE};
                permission.setPermissionArray(permissionArray);
                assertEquals(permissionArray.length, permission.getPermissionArray().length);
                manager.requestPermission(permission).subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Permission permission) {
                        assertNotNull(permission.getPreferences());
                        assertFalse(permission.isGranted());
                        Log.d("TEST", "testing...");
                        assertTrue(permission.getHasAskedPermission());
                        assertFalse(permission.getNeverAskPermission());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        });
    }
}
