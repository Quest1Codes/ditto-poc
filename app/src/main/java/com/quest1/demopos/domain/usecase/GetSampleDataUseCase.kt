package com.quest1.demopos.domain.usecase

import com.quest1.demopos.data.model.SampleData
import com.quest1.demopos.data.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSampleDataUseCase @Inject constructor(
    private val sampleRepository: SampleRepository
) {
    operator fun invoke(): Flow<List<SampleData>> {
        return sampleRepository.getSampleData()
    }
}