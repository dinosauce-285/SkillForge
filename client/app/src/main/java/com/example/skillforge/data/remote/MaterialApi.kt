package com.example.skillforge.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MaterialApi {
    @Multipart
    @POST("lessons/{lessonId}/materials")
    suspend fun uploadMaterial(
        @Header("Authorization") token: String,
        @Path("lessonId") lessonId: String,
        @Part("title") title: RequestBody,
        @Part("type") type: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Any>
}