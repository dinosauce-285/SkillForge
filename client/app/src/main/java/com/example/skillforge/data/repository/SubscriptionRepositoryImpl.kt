package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.SubscriptionApi
import com.example.skillforge.data.remote.dto.CreateInstructorSubscriptionRequestDto
import com.example.skillforge.data.remote.dto.InstructorSubscriptionResponseDto
import com.example.skillforge.domain.model.InstructorSubscription
import com.example.skillforge.domain.model.InstructorSubscriptionActivation
import com.example.skillforge.domain.model.InstructorSubscriptionUser
import com.example.skillforge.domain.repository.SubscriptionRepository
import org.json.JSONArray
import org.json.JSONObject

private const val INSTRUCTOR_MOCK_PLAN_CODE = "INSTRUCTOR_MOCK_PLAN"

class SubscriptionRepositoryImpl(
    private val api: SubscriptionApi,
) : SubscriptionRepository {
    override suspend fun becomeInstructor(): Result<InstructorSubscriptionActivation> {
        return try {
            val response = api.createInstructorSubscription(
                CreateInstructorSubscriptionRequestDto(
                    mockPaymentConfirmed = true,
                    planCode = INSTRUCTOR_MOCK_PLAN_CODE,
                ),
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(
                    Exception(
                        extractErrorMessage(response.errorBody()?.string())
                            ?: "Failed to activate instructor subscription",
                    ),
                )
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to activate instructor subscription"))
        }
    }

    private fun InstructorSubscriptionResponseDto.toDomain(): InstructorSubscriptionActivation {
        return InstructorSubscriptionActivation(
            message = message,
            subscription = InstructorSubscription(
                id = subscription.id,
                planCode = subscription.planCode,
                status = subscription.status,
                paymentStatus = subscription.paymentStatus,
                amount = subscription.amount,
                currency = subscription.currency,
                mockPaymentReference = subscription.mockPaymentReference,
                startedAt = subscription.startedAt,
                expiresAt = subscription.expiresAt,
            ),
            user = InstructorSubscriptionUser(
                id = user.id,
                email = user.email,
                fullName = user.fullName,
                role = user.role,
            ),
        )
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return try {
            val json = JSONObject(errorBody)
            when (val message = json.opt("message")) {
                is JSONArray -> {
                    (0 until message.length())
                        .mapNotNull { index -> message.optString(index).takeIf { it.isNotBlank() } }
                        .joinToString(separator = "\n")
                        .takeIf { it.isNotBlank() }
                }
                is String -> message.takeIf { it.isNotBlank() }
                else -> json.optString("error").takeIf { it.isNotBlank() }
            }
        } catch (e: Exception) {
            errorBody.takeIf { it.isNotBlank() }
        }
    }
}
