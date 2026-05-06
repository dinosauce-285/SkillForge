package com.example.skillforge.domain.repository

import com.example.skillforge.domain.model.InstructorSubscriptionActivation

interface SubscriptionRepository {
    suspend fun becomeInstructor(): Result<InstructorSubscriptionActivation>
}
