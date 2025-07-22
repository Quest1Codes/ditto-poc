package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.data.model.payment.PaymentCard
import com.quest1.demopos.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import live.ditto.Ditto
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Inject

@HiltViewModel
class PresenceViewModel @Inject constructor(
    private val dittoManager: DittoManager
) : ViewModel() {
    val ditto: Ditto by lazy {
        dittoManager.requireDitto()
    }
}
