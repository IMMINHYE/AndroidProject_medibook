package org.third.medicalapp.pharmacy.util

import org.third.medicalapp.hospital.model.Hospital
import org.third.medicalapp.hospital.model.HospitalList
import org.third.medicalapp.pharmacy.model.Pharmacy
import org.third.medicalapp.pharmacy.model.PharmacyList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PharmacyNetworkService {

    @GET("api/ph/list")
    fun doGetPharmacyList(): Call<PharmacyList>
    @GET("api/ph/byId/{id}")
    fun doGetPharmacyId(@Path("id") id: Long): Call<Pharmacy>
    @GET("api/ph/byPhar/{pharmacy}")
    fun doGetPharmacyName(@Path("pharmacy") pharmacy: String): Call<PharmacyList>

    @GET("api/ph/byDong/{dong}")
    fun doGetDong(@Path("dong") dong: String): Call<PharmacyList>

    @GET("api/ph/search")
    fun doSearch(@Path("dong") dong: String, @Path("pharmacy") pharmacy:String): Call<PharmacyList>


    @POST("api/ph/insert")
    fun insert(@Body pharmacy: Pharmacy): Call<Boolean>
    @POST("api/ph/findid")
    fun findById(@Body pharmacyList: List<Long>): Call<PharmacyList>
}