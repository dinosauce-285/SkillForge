package com.example.skillforge.feature.instructor_portal.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.core.utils.FileUtil
import com.example.skillforge.domain.repository.MaterialRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun uploadFile(context: Context, token: String, lessonId: String, type: String, uri: Uri) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading

            try {
                // Ép việc đọc file sang luồng nền (IO), tránh đơ app khi file quá nặng
                val file = withContext(Dispatchers.IO) {
                    FileUtil.uriToFile(context, uri)
                }

                if (file == null) {
                    _uploadState.value = UploadState.Error("Cannot read file from device.")
                    return@launch
                }

                println("=== BẮT ĐẦU UPLOAD ===")
                println("Tên file: ${file.name}, Kích thước: ${file.length() / 1024} KB")

                val result = withContext(Dispatchers.IO) {
                    materialRepository.uploadMaterial(token, lessonId, "", type, file)
                }

                result.onSuccess {
                    println("=== UPLOAD THÀNH CÔNG ===")
                    _uploadState.value = UploadState.Success
                }
                    .onFailure {
                        println("=== UPLOAD THẤT BẠI ===")
                        it.printStackTrace() // Lệnh này sẽ in ra NGUYÊN NHÂN LỖI THẬT SỰ
                        _uploadState.value = UploadState.Error(it.message ?: "Upload error!")
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                _uploadState.value = UploadState.Error("System error: ${e.message}")
            }
        }
    }

    fun resetState() { _uploadState.value = UploadState.Idle }
}