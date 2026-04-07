package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.MaterialApi
import com.example.skillforge.domain.repository.MaterialRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MaterialRepositoryImpl(
    private val api: MaterialApi
) : MaterialRepository {

    override suspend fun uploadMaterial(
        token: String,
        lessonId: String,
        title: String,
        type: String,
        file: File
    ): Result<Any> {
        return try {
            // 1. Chuyển Text thành RequestBody
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())

            // 2. Định dạng loại file (MimeType)
            val mimeType = when (file.extension.lowercase()) {
                "pdf" -> "application/pdf"
                "mp4" -> "video/mp4"
                "zip" -> "application/zip"
                "doc", "docx" -> "application/msword"
                else -> "application/octet-stream" // Mặc định
            }

            // 3. Đóng gói File thành MultipartBody.Part
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())

            // 🌟 Chữ "file" ở đây PHẢI KHỚP với tên field @UploadedFile('file') bên NestJS
            val fileBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // 4. Bắn API
            val response = api.uploadMaterial("Bearer $token", lessonId, titleBody, typeBody, fileBody)

            if (response.isSuccessful) {
                Result.success(response.body() ?: Any())
            } else {
                Result.failure(Exception("Upload thất bại: Lỗi ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}