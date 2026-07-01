package com.example.mobilneprojekat.ui.screens.online

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilneprojekat.data.GameData
import com.example.mobilneprojekat.data.model.OnlineMatch
import com.example.mobilneprojekat.data.repository.MatchRepository

@Composable
fun OnlineSpojniceScreen(
    navController: NavController,
    matchId: String,
    match: OnlineMatch,
    uid: String
) {
    val myPlayer = match.playerNumber(uid) ?: return
    val isMyTurn = match.isMyTurn(uid)

    val leftItems = GameData.spojnicePairs.map { it.first }
    val rightItems = if (match.rightOrder.isNotEmpty()) {
        match.rightOrder
    } else {
        GameData.spojnicePairs.map { it.second }
    }

    val matchedPairs = match.matches.mapNotNull { encoded ->
        val parts = encoded.split("|")
        if (parts.size == 2) parts[0] to parts[1] else null
    }

    val currentLeft = if (match.currentIndex < leftItems.size) {
        leftItems[match.currentIndex]
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        OnlinePlayerHeader(match, myPlayer, isMyTurn)

        Spacer(modifier = Modifier.height(12.dp))
        Text("P1: ${match.player1Score}  |  P2: ${match.player2Score}")
        Text("Par ${match.currentIndex + 1}/${leftItems.size}")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Levi pojmovi", style = MaterialTheme.typography.titleMedium)
                leftItems.forEachIndexed { index, item ->
                    val isActive = index == match.currentIndex
                    val isMatched = matchedPairs.any { it.first == item }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                when {
                                    isMatched -> Color(0xFF2196F3)
                                    isActive -> Color(0xFFE3F2FD)
                                    else -> Color(0xFFF5F5F5)
                                }
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item)
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Desni pojmovi", style = MaterialTheme.typography.titleMedium)
                rightItems.forEach { right ->
                    val isMatched = matchedPairs.any { it.second == right }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isMatched) Color(0xFF2196F3) else Color(0xFFC8E6C9)
                            )
                            .clickable(enabled = isMyTurn && currentLeft != null && !isMatched) {
                                val correctRight = GameData.spojnicePairs
                                    .find { it.first == currentLeft }?.second
                                val isCorrect = right == correctRight
                                val newMatches = match.matches.toMutableList()
                                if (isCorrect) {
                                    newMatches.add("$currentLeft|$right")
                                }
                                val p1Score = if (isCorrect && myPlayer == 1) {
                                    match.player1Score + 2
                                } else match.player1Score
                                val p2Score = if (isCorrect && myPlayer == 2) {
                                    match.player2Score + 2
                                } else match.player2Score

                                val nextIndex = match.currentIndex + 1
                                if (nextIndex >= leftItems.size) {
                                    val msg = when {
                                        p1Score > p2Score -> "Igrač 1 pobedio!"
                                        p2Score > p1Score -> "Igrač 2 pobedio!"
                                        else -> "Nerešeno!"
                                    }
                                    MatchRepository.updateMatch(
                                        matchId,
                                        mapOf(
                                            "matches" to newMatches,
                                            "currentIndex" to nextIndex,
                                            "player1Score" to p1Score,
                                            "player2Score" to p2Score
                                        )
                                    )
                                    MatchRepository.finishMatch(matchId, msg)
                                } else {
                                    val nextPlayer = if (match.currentPlayer == 1) 2 else 1
                                    MatchRepository.updateMatch(
                                        matchId,
                                        mapOf(
                                            "matches" to newMatches,
                                            "currentIndex" to nextIndex,
                                            "currentPlayer" to nextPlayer,
                                            "player1Score" to p1Score,
                                            "player2Score" to p2Score
                                        )
                                    )
                                }
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(right)
                    }
                }
            }
        }

        if (!isMyTurn) {
            Text("Čekate protivnika...")
        }
    }
}
