package com.example.skillforge.core.navigation

import com.example.skillforge.domain.model.AuthSession

sealed interface AppRoute {
    data object Login : AppRoute

    data class StudentCourseListing(
        val session: AuthSession,
    ) : AppRoute

    data class InstructorPortal(
        val session: AuthSession,
    ) : AppRoute
}
