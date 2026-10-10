package com.example.triptogether.data

import com.example.triptogether.model.ActivityItem
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalTime

class PlanRepository : PlanDataSource {
    private fun day(creator: String, tripId: String, index: Int) =
        FirebaseDatabase.getInstance().getReference("trips/$creator/$tripId/plan/day$index")

    override suspend fun activities(
        creator: String,
        tripId: String,
        index: Int,
    ): List<Pair<ActivityItem, String>> =
        day(creator, tripId, index)
            .get()
            .awaitResult()
            .children
            .mapNotNull { snapshot ->
                val item = snapshot.getValue(ActivityItem::class.java)
                val key = snapshot.key
                if (item == null || key == null) null else item to key
            }
            .sortedBy {
                runCatching { LocalTime.parse(it.first.time.padStart(5, '0')) }
                    .getOrDefault(LocalTime.MAX)
            }

    override suspend fun save(
        creator: String,
        tripId: String,
        index: Int,
        key: String?,
        item: ActivityItem,
    ) {
        val ref = day(creator, tripId, index)
        (if (key == null) ref.push() else ref.child(key)).setValue(item).awaitResult()
    }

    override suspend fun delete(creator: String, tripId: String, index: Int, key: String) {
        day(creator, tripId, index).child(key).removeValue().awaitResult()
    }
}
