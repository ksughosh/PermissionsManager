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

package com.skumar.manager.data

sealed class PermissionResponse {
    abstract val permissions: Array<out String>

    data class Granted internal constructor(
            private val permission: Permission
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }

    data class Denied internal constructor(
            private val permission: Permission
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }

    data class DeniedForever internal constructor(
            private val permission: Permission
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }

    class Mixed internal constructor(
            private val permission: Permission,
            vararg val responses: PermissionResponse
    ) : PermissionResponse() {
        override val permissions: Array<out String>
            get() = permission.permissionArray
    }
}
