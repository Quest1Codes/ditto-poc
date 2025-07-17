package com.quest1.demopos.domain.usecase


import com.quest1.demopos.data.model.SampleData
import com.quest1.demopos.data.repository.DittoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSampleDataUseCase @Inject constructor(
    private val dittoRepository: DittoRepository
) {
    operator fun invoke(): Flow<List<SampleData>> {
        val collectionName = "sampleData"
        val query = "SELECT * FROM $collectionName"

        dittoRepository.startSubscription(query)

        return dittoRepository.observeCollection(query).map { documents: List<Map<String, Any?>> ->
            documents.map { docMap ->
                SampleData(
                    id = docMap["id"] as? String ?: "",
                    name = docMap["name"] as? String ?: "No Name",
                    // FIX: Map the price from the document map
                    price = docMap["price"] as? Double
                )
            }
        }
    }
}