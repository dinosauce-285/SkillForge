package com.example.skillforge.core.di

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.repository.AuthRepositoryImpl
import com.example.skillforge.domain.repository.AuthRepository
import com.example.skillforge.domain.usecase.LoginUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    // 1. Khởi tạo Retrofit (Cần thêm thư viện Gson và Retrofit vào build.gradle)
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.15.159:3000/") // TODO: Thay bằng URL API thực tế của đồ án
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 2. Khởi tạo Api
    private val authApi = retrofit.create(AuthApi::class.java)

    // 3. Khởi tạo Repository
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi)

    // 4. Khởi tạo UseCase (Cái này sẽ được truyền vào ViewModel)
    val loginUseCase = LoginUseCase(authRepository)
}