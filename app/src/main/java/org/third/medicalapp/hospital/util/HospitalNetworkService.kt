package org.third.medicalapp.hospital.util

import org.third.medicalapp.hospital.model.Hospital
import org.third.medicalapp.hospital.model.HospitalList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HospitalNetworkService {
    @GET("home")
    fun doHome(): Call<String>
    //병원리스트 전체 불러오기
    @GET("api/hospital/list")
    fun doGetHospitalList(): Call<HospitalList>
    //상세보기시 그 병원id에 맞는 것 불러오기
    @GET("api/hospital/byId/{id}")
    fun doGetHospitalId(@Path("id") id: Long): Call<Hospital>

    //이름 검색시 관련된 병원리스트 불러오기
    @GET("api/hospital/byName/{hname}")
    fun doGetHospitalName(@Path("hname") name: String): Call<HospitalList>

    //진료과에 해당하는 병원리스트 불러오기 -> DepartSelectActivity
    @GET("api/hospital/byCode/{hcode}")
    fun doGetHcode(@Path("hcode") hcode: String): Call<HospitalList>

    //검색한 동에 해당하는 병원리스트 불러오기 -> LocationSelectActivity
    @GET("api/hospital/byDong/{dong}")
    fun doGetDong(@Path("dong") dong: String): Call<HospitalList>

    @GET("api/hospital/byCity/{city}")
    fun doGetCity(@Path("city") city: String): Call<HospitalList>
    @GET("api/hospital/bySigun/{sigun}")
    fun doGetSignun(@Path("sigun") sigun: String): Call<HospitalList>
    // 지역과 진료과 동시검색기능
    @GET("api/hospital/search")
    fun doSearch(@Path("hcode") hcode: String, @Path("dong") dong: String, @Path("hname") hname:String): Call<HospitalList>

    @POST("api/hospital/insert")
    fun insert(@Body hospital: Hospital): Call<Boolean>
    @POST("api/hospital/findid")
    fun findById(@Body hospitalList: List<Long>): Call<HospitalList>


}