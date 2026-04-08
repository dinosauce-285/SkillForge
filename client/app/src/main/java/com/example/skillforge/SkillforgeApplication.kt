package com.example.skillforge

import android.app.Application
import com.example.skillforge.core.di.AppContainer

class SkillforgeApplication : Application() {
    // translated comment
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
