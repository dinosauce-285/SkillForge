package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.MaterialRepository

@Suppress("UNCHECKED_CAST")
class MaterialUploadViewModelFactory(
    private val materialRepository: MaterialRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MaterialUploadViewModel::class.java)) {
            return MaterialUploadViewModel(materialRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}