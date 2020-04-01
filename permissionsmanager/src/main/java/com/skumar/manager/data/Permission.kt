package com.skumar.manager.data

import android.os.Parcel
import android.os.Parcelable

internal data class Permission(val permissionArray: Array<out String>) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createStringArray() ?: arrayOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Permission) return false
        return permissionArray.contentEquals(other.permissionArray)
    }

    override fun hashCode(): Int = permissionArray.contentHashCode()
    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeStringArray(permissionArray)
    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Permission> {
        override fun createFromParcel(parcel: Parcel): Permission {
            return Permission(parcel)
        }

        override fun newArray(size: Int): Array<Permission?> {
            return arrayOfNulls(size)
        }
    }
}


