package com.example.todoapp

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


    data class LoginRequest(val email:String,val password: String)
    data class RegisterRequest(val username:String,val email:String,val phone: String,val password: String)
    data class Booking(val email:String,val check_in:String,val check_out:String)
    data class CheckIn(val email:String,val roomNumber: Number,val code:String)
    data class MessageResponse(
        val message: String
    )
    data class AuthResponse(
        val message : String,
        val token :String
    )
    data class MessageBooking(
        val message:String,
        val roomNumber:Number
    )
    interface ApiService {
        @POST("checkInRoom")
        fun checkIn(@Body request: CheckIn):Call<ResponseBody>
        @POST("booking")
        fun booking(@Body request: Booking):Call<ResponseBody>
        @POST("login")
        fun login(@Body request: LoginRequest): Call<ResponseBody>

        @POST("register")
        fun regiter(@Body request: RegisterRequest): Call<ResponseBody>

        @POST("logout")
        fun logout(): Call<Void>
    }
