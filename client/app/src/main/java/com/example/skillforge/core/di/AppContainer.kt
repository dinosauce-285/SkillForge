package com.example.skillforge.core.di

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.data.repository.AuthRepositoryImpl
import com.example.skillforge.data.repository.CourseRepositoryImpl
import com.example.skillforge.domain.repository.AuthRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.usecase.LoginUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    // Android Emulator phải dùng 10.0.2.2 để truy cập localhost của máy chạy backend.
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 2. Khởi tạo Api
    private val authApi = retrofit.create(AuthApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)

    // 3. Khởi tạo Repository
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi)
    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi)

    // 4. Khởi tạo UseCase (Cái này sẽ được truyền vào ViewModel)
    val loginUseCase = LoginUseCase(authRepository)
}
