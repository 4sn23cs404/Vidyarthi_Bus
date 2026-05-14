package com.example.vidyarthibus3.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BusRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getRoutes(): List<BusRoute> {
        return try {
            db.collection("routes").get().await().toObjects(BusRoute::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun listenToReports(routeId: String): Flow<List<BusReport>> = callbackFlow {
        val subscription = db.collection("reports")
            .whereEqualTo("routeId", routeId)
            .orderBy("reportedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val reports = snapshot?.toObjects(BusReport::class.java) ?: emptyList()

                // ✅ Only keep reports from last 5 minutes
                val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
                val recentReports = reports.filter { report ->
                    val reportTime = report.reportedAt?.toDate()?.time ?: 0L
                    reportTime >= fiveMinutesAgo
                }

                // ✅ Majority voting on recent reports only
                val voteCounts = recentReports
                    .groupBy { it.status }
                    .mapValues { it.value.size }

                val majorityStatus = voteCounts.maxByOrNull { it.value }?.key

                val consensusReports = if (majorityStatus != null && recentReports.isNotEmpty()) {
                    recentReports.sortedByDescending { it.status == majorityStatus }
                        .map { if (it == recentReports.first()) it.copy(status = majorityStatus) else it }
                } else {
                    // ✅ If no reports in last 5 min → return empty so UI shows "NO RECENT REPORTS"
                    emptyList()
                }

                trySend(consensusReports)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun submitReport(routeId: String, status: CrowdStatus, userId: String) {
        val report = mapOf(
            "routeId" to routeId,
            "status" to status.name,
            "userId" to userId,
            "reportedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
            "lat" to 12.9716,
            "lng" to 77.5946
        )
        db.collection("reports").add(report).await()
    }
}