package com.quest1.demopos.ui.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quest1.demopos.domain.usecase.GetSampleDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SampleViewModel @Inject constructor(
    private val getSampleDataUseCase: GetSampleDataUseCase
) : ViewModel() {

    private val TAG = "SampleViewModel"

    init {
        fetchData()
    }

    private fun fetchData() {
        // Launch a coroutine to collect the data from the Flow
        viewModelScope.launch {
            Log.d(TAG, "Calling GetSampleDataUseCase...")
            getSampleDataUseCase()
                .catch { e ->
                    // Catch any errors during the flow
                    Log.e(TAG, "Error collecting data: ${e.message}")
                }
                .collect { dataList ->
                    // This block will execute when the Flow emits data
                    Log.d(TAG, "Data received from UseCase: $dataList")
                }
        }
    }
}
