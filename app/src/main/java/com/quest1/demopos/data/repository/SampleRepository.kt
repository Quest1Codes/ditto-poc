package com.quest1.demopos.data.repository

import com.quest1.demopos.data.model.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleRepository {

    fun getSampleData(): Flow<List<SampleData>> = flow {
        val data = listOf(
            SampleData("1", "Item 1"),
            SampleData("2", "Item 2"),
            SampleData("3", "Item 3")

        )
        emit(data)
    }
}