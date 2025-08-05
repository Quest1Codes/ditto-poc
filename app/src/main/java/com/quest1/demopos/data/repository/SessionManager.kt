package com.quest1.demopos.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SessionManager @Inject constructor() {
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    fun userLoggedIn(userId: String) {
        _currentUserId.value = userId
    }

    fun logout() {
        _currentUserId.value = null
    }
}