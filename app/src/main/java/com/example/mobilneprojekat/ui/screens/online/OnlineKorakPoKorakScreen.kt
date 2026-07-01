package com.example.mobilneprojekat.ui.screens.online

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilneprojekat.data.GameData
import com.example.mobilneprojekat.data.model.OnlineMatch
import com.example.mobilneprojekat.data.repository.MatchRepository
import kotlinx.coroutines.delay

private const val ROUND_TIME_SECONDS = 70
private const val MAX_ROUNDS = 3

@Composable
fun OnlineKorakPoKorakScreen(
    navController: NavController,
    matchId: String,
    match: OnlineMatch,
    uid: String
) {
    val myPlayer = match.playerNumber(uid) ?: return
    val isMyTurn = match.isMyTurn(uid)
    val puzzle = GameData.korakPuzzles[match.puzzleIndex % GameData.korakPuzzles.size]

    var answerInput by remember(match.puzzleIndex, match.currentPlayer) { mutableStateOf("") }
    var localSeconds by remember(match.roundStartedAt, match.puzzleIndex) {
        mutableIntStateOf(ROUND_TIME_SECONDS)
    }

    LaunchedEffect(match.roundStartedAt, match.puzzleIndex, match.currentPlayer) {
        localSeconds = ROUND_TIME_SECONDS
        while (localSeconds > 0) {
            delay(1000)
            localSeconds--
            if (isMyTurn && localSeconds > 0 && localSeconds % 10 == 0) {
                val newHints = (match.revealedHints + 1)
                    .coerceAtMost(puzzle.hints.size)
                if (newHints > match.revealedHints) {
                    MatchRepository.updateMatch(
                        matchId,
                        mapOf("revealedHints" to newHints)
                    )
                }
            }
        }
        if (isMyTurn) {
            advanceTurn(matchId, match, timedOut = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        OnlinePlayerHeader(match, myPlayer, isMyTurn)

        Spacer(modifier = Modifier.height(12.dp))
        Text("P1: ${match.player1Score}  |  P2: ${match.player2Score}")
        Text("Runda ${match.round}/$MAX_ROUNDS")
        Text("Preostalo vreme: $localSeconds s")
        if (match.message.isNotEmpty()) {
            Text(match.message, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(puzzle.hints.take(match.revealedHints)) { hint ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(text = hint, modifier = Modifier.padding(20.dp))
                }
            }
        }

        if (isMyTurn) {
            OutlinedTextField(
                value = answerInput,
                onValueChange = { answerInput = it },
                label = { Text("Unesi odgovor") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val correct = answerInput.trim()
                        .equals(puzzle.answer, ignoreCase = true)
                    if (correct) {
                        val earned = (20 - ((match.revealedHints - 1) * 2)).coerceAtLeast(5)
                        val newP1 = if (myPlayer == 1) match.player1Score + earned else match.player1Score
                        val newP2 = if (myPlayer == 2) match.player2Score + earned else match.player2Score
                        advanceTurn(
                            matchId,
                            match.copy(player1Score = newP1, player2Score = newP2),
                            timedOut = false,
                            message = "Tačno! +$earned poena"
                        )
                    } else {
                        MatchRepository.updateMatch(
                            matchId,
                            mapOf("message" to "Netačan odgovor")
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Potvrdi")
            }
        } else {
            Text(
                "Čekate protivnika...",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun advanceTurn(
    matchId: String,
    match: OnlineMatch,
    timedOut: Boolean,
    message: String = if (timedOut) "Vreme isteklo!" else ""
) {
    val nextPlayer = if (match.currentPlayer == 1) 2 else 1
    var nextRound = match.round
    if (nextPlayer == 1) nextRound++

    if (nextRound > MAX_ROUNDS) {
        val msg = when {
            match.player1Score > match.player2Score -> "Igrač 1 pobedio!"
            match.player2Score > match.player1Score -> "Igrač 2 pobedio!"
            else -> "Nerešeno!"
        }
        MatchRepository.updateMatch(
            matchId,
            mapOf(
                "player1Score" to match.player1Score,
                "player2Score" to match.player2Score,
                "message" to msg
            )
        )
        MatchRepository.finishMatch(matchId, msg)
        return
    }

    val nextPuzzle = (match.puzzleIndex + 1) % GameData.korakPuzzles.size
    MatchRepository.updateMatch(
        matchId,
        mapOf(
            "currentPlayer" to nextPlayer,
            "round" to nextRound,
            "puzzleIndex" to nextPuzzle,
            "revealedHints" to 1,
            "roundStartedAt" to System.currentTimeMillis(),
            "player1Score" to match.player1Score,
            "player2Score" to match.player2Score,
            "message" to message
        )
    )
}

@Composable
internal fun OnlinePlayerHeader(match: OnlineMatch, myPlayer: Int, isMyTurn: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerBadge("P1", myPlayer == 1, match.currentPlayer == 1)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (isMyTurn) "Vaš potez" else "Potez protivnika",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        PlayerBadge("P2", myPlayer == 2, match.currentPlayer == 2)
    }
}

@Composable
internal fun PlayerBadge(label: String, isMe: Boolean, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                when {
                    isActive -> Color.Green
                    isMe -> Color(0xFF3F51B5)
                    else -> Color(0xFFE91E63)
                }
            )
            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White)
    }
}
