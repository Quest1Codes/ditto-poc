package com.quest1.demopos.ui.view

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
