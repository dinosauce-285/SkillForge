package com.example.skillforge.feature.instructor_portal.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.core.utils.FileUtil
import com.example.skillforge.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UploadState {
    object Idle : UploadState()
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}

class MaterialUploadViewModel(
    private val materialRepository: MaterialRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    fun uploadFile(context: Context, token: String, lessonId: String, title: String, type: String, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            val file = FileUtil.uriToFile(context, uri)
            if (file == null) {
                _uploadState.value = UploadState.Error("Cannot read file. Please try again.")
                return@launch
            }

            materialRepository.uploadMaterial(token, lessonId, title, type, file)
                .onSuccess {
                    _uploadState.value = UploadState.Success
                }
                .onFailure {
                    _uploadState.value = UploadState.Error(it.message ?: "Upload error!")
                }
        }
    }

    fun resetState() { _uploadState.value = UploadState.Idle }
}