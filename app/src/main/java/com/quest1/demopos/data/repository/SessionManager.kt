package com.quest1.demopos.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    private val _activeOrderId = MutableStateFlow<String?>(null)
    val activeOrderId = _activeOrderId.asStateFlow()

    fun userLoggedIn(userId: String) {
        _currentUserId.value = userId
    }

    fun setActiveOrderId(orderId: String) {
        _activeOrderId.value = orderId
    }

    fun clearActiveOrderId() {
        _activeOrderId.value = null
    }

    fun logout() {
        _currentUserId.value = null
        clearActiveOrderId()
    }
}