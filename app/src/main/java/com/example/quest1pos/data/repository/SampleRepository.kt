package com.example.quest1pos.data.repository

import android.util.Log
import com.example.quest1pos.data.model.SampleData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import live.ditto.ditto_wrapper.DittoManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The repository is now set up for dependency injection with Hilt.
 * We inject the DittoManager via the constructor, which Hilt will provide.
 */
@Singleton
class SampleRepository @Inject constructor(
    private val dittoManager: DittoManager
) {

    private val TAG = "SampleRepository"

    /**
     * An init block is a great place to start background tasks.
     * Here, we start observing the "items" collection from Ditto.
     * This won't affect the UI yet, but you can see Ditto working in the Logcat.
     */
    init {
        val ditto = dittoManager.requireDitto()

        // THE FIX IS HERE: Use `registerObserver` on the store directly.
        ditto.store.registerObserver(
            query = "SELECT * FROM items"
        ) { result ->
            // This log will appear whenever data in the "items" collection changes.
            Log.i(TAG, "Ditto is observing documents in the 'items' collection.")
        }
    }

    /**
     * This function is unchanged.
     * It continues to provide the hardcoded list of data to the UI.
     */
    fun getSampleData(): Flow<List<SampleData>> = flow {
        val data = listOf(
            SampleData("1", "Item 1"),
            SampleData("2", "Item 2"),
            SampleData("3", "Item 3")
        )
        emit(data)
    }
}
