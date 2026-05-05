package com.example.skillforge.feature.admin_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.Course
import com.example.skillforge.domain.model.CourseStructure
import com.example.skillforge.domain.model.User
import com.example.skillforge.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _courseQueue = MutableStateFlow<List<Course>>(emptyList())
    val courseQueue: StateFlow<List<Course>> = _courseQueue.asStateFlow()

    private val _coursePreview = MutableStateFlow<CourseStructure?>(null)
    val coursePreview: StateFlow<CourseStructure?> = _coursePreview.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchUsers(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = adminRepository.getAllUsers(token)
            result.onSuccess {
                _users.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun toggleUserBan(token: String, id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.toggleUserBan(token, id)
            result.onSuccess { updatedUser ->
                _users.value = _users.value.map { if (it.id == updatedUser.id) updatedUser else it }
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun createInstructor(token: String, email: String, fullName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.createInstructor(token, email, fullName)
            result.onSuccess {
                fetchUsers(token)
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun fetchCourseQueue(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.getCourseQueue(token)
            result.onSuccess {
                _courseQueue.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun fetchCoursePreview(token: String, id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.getCoursePreview(token, id)
            result.onSuccess {
                _coursePreview.value = it
            }.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun moderateCourse(token: String, id: String, status: String, level: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = adminRepository.moderateCourse(token, id, status, level)
            if (result.isSuccess) {
                // If we are looking at the queue, refresh it
                _courseQueue.value = _courseQueue.value.filter { it.id != id }
                // Also clear preview if it was this course
                if (_coursePreview.value?.course?.id == id) {
                    _coursePreview.value = null
                }
            } else {
                _error.value = result.exceptionOrNull()?.message
            }
            _isLoading.value = false
        }
    }
}

class AdminViewModelFactory(
    private val adminRepository: AdminRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(adminRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
