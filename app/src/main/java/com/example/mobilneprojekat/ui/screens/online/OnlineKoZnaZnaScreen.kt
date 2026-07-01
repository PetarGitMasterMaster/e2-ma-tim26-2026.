package com.example.mobilneprojekat.ui.screens.online

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
import kotlinx.coroutines.delay

private const val TOTAL_QUESTIONS = 5

@Composable
fun OnlineKoZnaZnaScreen(
    navController: NavController,
    matchId: String,
    match: OnlineMatch,
    uid: String
) {
    val myPlayer = match.playerNumber(uid) ?: return
    val isMyTurn = match.isMyTurn(uid)
    val question = GameData.triviaQuestions[
        match.questionIndex.coerceAtMost(GameData.triviaQuestions.lastIndex)
    ]

    var selectedAnswer by remember(match.questionIndex, match.currentPlayer) {
        mutableStateOf<String?>(null)
    }
    var answerLocked by remember(match.questionIndex, match.currentPlayer) {
        mutableStateOf(false)
    }

    LaunchedEffect(match.phase, match.questionIndex) {
        if (match.phase == "result" && isMyTurn) {
            delay(1500)
            advanceQuestion(matchId, match)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OnlinePlayerHeader(match, myPlayer, isMyTurn)

        Spacer(modifier = Modifier.height(16.dp))
        Text("P1: ${match.player1Score}  |  P2: ${match.player2Score}")
        Text("Pitanje ${match.questionIndex + 1}/$TOTAL_QUESTIONS")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        question.answers.forEach { answer ->
            val buttonColor = when {
                !answerLocked -> MaterialTheme.colorScheme.primary
                answer == question.correctAnswer -> Color.Green
                answer == selectedAnswer -> Color.Red
                else -> MaterialTheme.colorScheme.primary
            }

            Button(
                onClick = {
                    if (!isMyTurn || answerLocked) return@Button
                    selectedAnswer = answer
                    answerLocked = true
                    val correct = answer == question.correctAnswer
                    val delta = if (correct) 10 else -5
                    val updates = mutableMapOf<String, Any>(
                        "message" to if (correct) "Tačno!" else "Netačno!",
                        "phase" to "result"
                    )
                    if (myPlayer == 1) {
                        updates["player1Score"] = match.player1Score + delta
                    } else {
                        updates["player2Score"] = match.player2Score + delta
                    }
                    MatchRepository.updateMatch(matchId, updates)
                },
                enabled = isMyTurn && !answerLocked,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(answer)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!isMyTurn) {
            Text("Čekate protivnika da odgovori...")
        }
    }
}

private fun advanceQuestion(matchId: String, match: OnlineMatch) {
    val nextIndex = match.questionIndex + 1
    if (nextIndex >= TOTAL_QUESTIONS) {
        val msg = when {
            match.player1Score > match.player2Score -> "Igrač 1 pobedio!"
            match.player2Score > match.player1Score -> "Igrač 2 pobedio!"
            else -> "Nerešeno!"
        }
        MatchRepository.finishMatch(matchId, msg)
        return
    }

    val nextPlayer = if (match.currentPlayer == 1) 2 else 1
    MatchRepository.updateMatch(
        matchId,
        mapOf(
            "questionIndex" to nextIndex,
            "currentPlayer" to nextPlayer,
            "message" to "",
            "phase" to "playing"
        )
    )
}
