package com.example.mobilneprojekat.data.repository

import com.example.mobilneprojekat.data.GameData
import com.example.mobilneprojekat.data.model.OnlineMatch
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object MatchRepository {

    private val db = FirebaseFirestore.getInstance()

    fun parseMatch(snapshot: DocumentSnapshot): OnlineMatch {
        @Suppress("UNCHECKED_CAST")
        val numbers = snapshot.get("generatedNumbers") as? List<Long>
        @Suppress("UNCHECKED_CAST")
        val matches = snapshot.get("matches") as? List<String>
        @Suppress("UNCHECKED_CAST")
        val rightOrder = snapshot.get("rightOrder") as? List<String>

        return OnlineMatch(
            id = snapshot.id,
            player1 = snapshot.getString("player1") ?: "",
            player2 = snapshot.getString("player2"),
            status = snapshot.getString("status") ?: "waiting",
            gameType = snapshot.getString("gameType") ?: "",
            player1Score = snapshot.getLong("player1Score")?.toInt() ?: 0,
            player2Score = snapshot.getLong("player2Score")?.toInt() ?: 0,
            currentPlayer = snapshot.getLong("currentPlayer")?.toInt() ?: 1,
            round = snapshot.getLong("round")?.toInt() ?: 1,
            phase = snapshot.getString("phase") ?: "playing",
            puzzleIndex = snapshot.getLong("puzzleIndex")?.toInt() ?: 0,
            revealedHints = snapshot.getLong("revealedHints")?.toInt() ?: 1,
            targetNumber = snapshot.getLong("targetNumber")?.toInt(),
            generatedNumbers = numbers?.map { it.toInt() } ?: emptyList(),
            player1Result = snapshot.getLong("player1Result")?.toInt() ?: 0,
            player2Result = snapshot.getLong("player2Result")?.toInt() ?: 0,
            questionIndex = snapshot.getLong("questionIndex")?.toInt() ?: 0,
            currentIndex = snapshot.getLong("currentIndex")?.toInt() ?: 0,
            matches = matches ?: emptyList(),
            rightOrder = rightOrder ?: emptyList(),
            roundStartedAt = snapshot.getLong("roundStartedAt") ?: 0L,
            message = snapshot.getString("message") ?: ""
        )
    }

    fun listenToMatch(
        matchId: String,
        onUpdate: (OnlineMatch) -> Unit
    ): ListenerRegistration {
        return db.collection("matches").document(matchId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    onUpdate(parseMatch(snapshot))
                }
            }
    }

    fun findOrCreateMatch(
        gameType: String,
        uid: String,
        onMatchId: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("matches")
            .whereEqualTo("status", "waiting")
            .whereEqualTo("gameType", gameType)
            .get()
            .addOnSuccessListener { result ->
                val joinable = result.documents.firstOrNull { doc ->
                    doc.getString("player1") != uid
                }

                if (joinable != null) {
                    val matchId = joinable.id
                    db.collection("matches").document(matchId)
                        .update(
                            mapOf(
                                "player2" to uid,
                                "status" to "active",
                                "roundStartedAt" to System.currentTimeMillis()
                            )
                        )
                        .addOnSuccessListener { onMatchId(matchId) }
                        .addOnFailureListener { onError(it.message ?: "Greška pri pridruživanju") }
                        .addOnFailureListener { onError(it.message ?: "Greška pri pridruživanju") }
                } else {
                    val seed = System.currentTimeMillis()
                    val (target, numbers) = GameData.generateMojBrojNumbers(seed)
                    val rightOrder = GameData.spojnicePairs.map { it.second }.shuffled(
                        kotlin.random.Random(seed)
                    )

                    val newMatch = hashMapOf(
                        "player1" to uid,
                        "player2" to null,
                        "status" to "waiting",
                        "gameType" to gameType,
                        "player1Score" to 0,
                        "player2Score" to 0,
                        "currentPlayer" to 1,
                        "round" to 1,
                        "phase" to "playing",
                        "puzzleIndex" to 0,
                        "revealedHints" to 1,
                        "targetNumber" to target,
                        "generatedNumbers" to numbers,
                        "player1Result" to 0,
                        "player2Result" to 0,
                        "questionIndex" to 0,
                        "currentIndex" to 0,
                        "matches" to emptyList<String>(),
                        "rightOrder" to rightOrder,
                        "roundStartedAt" to System.currentTimeMillis(),
                        "message" to "",
                        "seed" to seed
                    )

                    db.collection("matches")
                        .add(newMatch)
                        .addOnSuccessListener { onMatchId(it.id) }
                        .addOnFailureListener { onError(it.message ?: "Greška pri kreiranju meča") }
                }
            }
            .addOnFailureListener { onError(it.message ?: "Greška pri pretrazi") }
    }

    fun updateMatch(matchId: String, fields: Map<String, Any?>) {
        val cleaned = fields.filterValues { it != null }.mapValues { it.value!! }
        if (cleaned.isNotEmpty()) {
            db.collection("matches").document(matchId).update(cleaned)
        }
    }

    fun finishMatch(matchId: String, message: String) {
        updateMatch(
            matchId,
            mapOf(
                "status" to "finished",
                "phase" to "finished",
                "message" to message
            )
        )
    }

    fun cancelMatch(matchId: String, uid: String) {
        db.collection("matches").document(matchId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener
                val player1 = doc.getString("player1")
                val player2 = doc.getString("player2")
                if (player2 == null && player1 == uid) {
                    doc.reference.delete()
                } else {
                    updateMatch(
                        matchId,
                        mapOf(
                            "status" to "finished",
                            "message" to "Meč je otkazan"
                        )
                    )
                }
            }
    }
}
