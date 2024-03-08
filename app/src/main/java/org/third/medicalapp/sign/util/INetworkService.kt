package org.third.medicalapp.sign.util

import org.third.medicalapp.sign.model.UserModel
import org.third.medicalapp.sign.model.UserModelList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface INetworkService {
    @GET("user/check/{nickName}")
    fun checkNick(@Path("nickName") nickName: String): Call<Boolean>
    @GET("user/checkUser/{userName}")
    fun checkUser(@Path("userName") userName: String) : Call<UserModel>
    @GET("user/list")
    fun getUserList():Call<UserModelList>
    @PUT("user/modify")
    fun modify(@Body userModel: UserModel):Call<Boolean>

    @POST("user/insert")
    fun insert(@Body userModel: UserModel):Call<Boolean>

    @DELETE("user/delete/{userName}")
    fun delete(@Path("userName") userName: String):Call<Boolean>
}