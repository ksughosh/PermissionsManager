package com.skumar.permissionsmanager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PermissionUnitTest {
    private Permission permission = new Permission(0);

    @Test
    public void test_PermissionRequestCode() throws Exception{
        assertEquals(permission.getRequestCode(), 0);
    }

    @Test
    public void test_PermissionString() throws Exception {
        Permission permission2 = new Permission(4);
        assertFalse(permission.equals(permission2));
    }
}