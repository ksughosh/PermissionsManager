package com.skumar.permissionsmanager

/**
 * Copyright (c) 2017.
 * @author Sughosh Krishna Kumar.
 */
@Suppress("unused")
class IllegalClassException: RuntimeException {
    constructor(): super()
    constructor(message:String): super(message)
    constructor(cause: Throwable): super(cause)
}