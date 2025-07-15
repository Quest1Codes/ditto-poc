package com.example.quest1pos.domain.usecase

import com.example.quest1pos.data.model.SampleData
import com.example.quest1pos.data.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSampleDataUseCase @Inject constructor(
    private val sampleRepository: SampleRepository
) {
    operator fun invoke(): Flow<List<SampleData>> {
        return sampleRepository.getSampleData()
    }
}