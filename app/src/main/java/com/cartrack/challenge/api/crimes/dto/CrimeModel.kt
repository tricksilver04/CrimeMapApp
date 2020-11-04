package com.cartrack.challenge.api.crimes.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

object CrimeModel {

    @Parcelize
    data class CrimeData (
        @SerializedName("category") val category : String,
        @SerializedName("location_type") val location_type : String,
        @SerializedName("location") val location : Location,
        @SerializedName("context") val context : String,
        @SerializedName("persistent_id") val persistent_id : String,
        @SerializedName("id") val id : Int,
        @SerializedName("location_subtype") val location_subtype : String,
        @SerializedName("month") val month : String
    ): Parcelable

    @Parcelize
    data class Location (
        @SerializedName("latitude") val latitude : Double,
        @SerializedName("street") val street : Street,
        @SerializedName("longitude") val longitude : Double
    ): Parcelable

    @Parcelize
    data class Street (
        @SerializedName("id") val id : Int,
        @SerializedName("name") val name : String
    ): Parcelable

}