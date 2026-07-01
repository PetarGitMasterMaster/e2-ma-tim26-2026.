package com.example.mobilneprojekat.data.model

data class OnlineMatch(
    val id: String = "",
    val player1: String = "",
    val player2: String? = null,
    val status: String = "waiting",
    val gameType: String = "",
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val currentPlayer: Int = 1,
    val round: Int = 1,
    val phase: String = "playing",
    val puzzleIndex: Int = 0,
    val revealedHints: Int = 1,
    val targetNumber: Int? = null,
    val generatedNumbers: List<Int> = emptyList(),
    val player1Result: Int = 0,
    val player2Result: Int = 0,
    val questionIndex: Int = 0,
    val currentIndex: Int = 0,
    val matches: List<String> = emptyList(),
    val rightOrder: List<String> = emptyList(),
    val roundStartedAt: Long = 0L,
    val message: String = ""
) {
    fun playerNumber(uid: String): Int? = when (uid) {
        player1 -> 1
        player2 -> 2
        else -> null
    }

    fun isMyTurn(uid: String): Boolean = playerNumber(uid) == currentPlayer

    fun scoreFor(player: Int): Int = if (player == 1) player1Score else player2Score
}
