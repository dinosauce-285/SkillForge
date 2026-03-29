package com.example.skillforge.core.di

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.data.repository.AuthRepositoryImpl
import com.example.skillforge.data.repository.CourseRepositoryImpl
import com.example.skillforge.domain.repository.AuthRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.usecase.LoginUseCase
import com.example.skillforge.domain.usecase.RegisterUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.20:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi = retrofit.create(AuthApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)

    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi)
    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi)

    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
}
