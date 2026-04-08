data class TransactionUiState(
    val course: Course, // Added course property
    val basePrice: Double, // Removed duplicate basePrice property
    val isLoading: Boolean = false, // Added isLoading field
    val isSubmitting: Boolean = false // Added isSubmitting field
)