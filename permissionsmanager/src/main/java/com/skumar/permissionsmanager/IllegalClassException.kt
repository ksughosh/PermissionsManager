

package com.loop.toolkit.kotlin.Utils.Exception

/**
 * Copyright (c) 2017. LOOP New Media GmbH.
 * @author Sughosh Krishna Kumar.
 */
@Suppress("unused")
open class IllegalClassException: RuntimeException {
    constructor(): super()
    constructor(message:String): super(message)
    constructor(cause: Throwable): super(cause)
    companion object {
        @JvmStatic private val serialVersionUID: Long = 123
    }
}