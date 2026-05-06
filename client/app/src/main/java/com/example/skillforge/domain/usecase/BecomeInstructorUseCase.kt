package com.example.skillforge.domain.usecase

import com.example.skillforge.domain.model.InstructorSubscriptionActivation
import com.example.skillforge.domain.repository.SubscriptionRepository

class BecomeInstructorUseCase(
    private val repository: SubscriptionRepository,
) {
    suspend operator fun invoke(): Result<InstructorSubscriptionActivation> {
        return repository.becomeInstructor()
    }
}
