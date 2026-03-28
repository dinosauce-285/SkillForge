package com.example.skillforge

import android.app.Application
import com.example.skillforge.core.di.AppContainer

class SkillforgeApplication : Application() {
    // Khởi tạo container một lần duy nhất
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}