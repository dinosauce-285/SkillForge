package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.CouponDto
import com.example.skillforge.domain.repository.CouponRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CouponViewModel(private val repository: CouponRepository) : ViewModel() {
    private val _coupons = MutableStateFlow<List<CouponDto>>(emptyList())
    val coupons: StateFlow<List<CouponDto>> = _coupons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchCoupons() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getInstructorCoupons()
            if (result.isSuccess) {
                _coupons.value = result.getOrNull() ?: emptyList()
            }
            _isLoading.value = false
        }
    }

    fun createCoupon(code: String, discountPercent: Int, isActive: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createCoupon(code, discountPercent, isActive)
            if (result.isSuccess) {
                fetchCoupons()
            }
            _isLoading.value = false
        }
    }

    fun deleteCoupon(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteCoupon(id)
            if (result.isSuccess) {
                fetchCoupons()
            }
            _isLoading.value = false
        }
    }
}

class CouponViewModelFactory(private val repository: CouponRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CouponViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CouponViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
