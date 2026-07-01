package com.example.mobilneprojekat.ui.screens.online

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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

private const val TOTAL_QUESTIONS = 5
private const val QUESTION_DELAY_MS = 3000L
private const val ANSWER_TIME_MS = 5000L

@Composable
fun OnlineKoZnaZnaScreen(
    navController: NavController,
    matchId: String,
    match: OnlineMatch,
    uid: String
) {
    val myPlayer = match.playerNumber(uid) ?: return

    val question = GameData.triviaQuestions[
        match.questionIndex.coerceAtMost(GameData.triviaQuestions.lastIndex)
    ]

    var now by remember(match.questionIndex, match.phase) {
        mutableStateOf(System.currentTimeMillis())
    }

    LaunchedEffect(match.questionIndex, match.phase, match.roundStartedAt) {
        while (match.phase == "playing") {
            now = System.currentTimeMillis()
            delay(250)
        }
    }

    val elapsedMs = (now - match.roundStartedAt).coerceAtLeast(0L)
    val showAnswers = elapsedMs >= QUESTION_DELAY_MS

    val answerElapsedMs = (elapsedMs - QUESTION_DELAY_MS).coerceAtLeast(0L)
    val questionTime = if (!showAnswers) {
        5
    } else {
        ((ANSWER_TIME_MS - answerElapsedMs + 999) / 1000)
            .toInt()
            .coerceIn(0, 5)
    }

    val myAnswer = if (myPlayer == 1) match.player1Answer else match.player2Answer
    val myAnsweredAt = if (myPlayer == 1) match.player1AnsweredAt else match.player2AnsweredAt
    val hasMyAnswered = myAnsweredAt > 0L

    val p1Answered = match.player1AnsweredAt > 0L
    val p2Answered = match.player2AnsweredAt > 0L
    val bothAnswered = p1Answered && p2Answered

    val answerTimeExpired = showAnswers && questionTime <= 0

    LaunchedEffect(
        match.phase,
        match.questionIndex,
        bothAnswered,
        answerTimeExpired,
        match.player1AnsweredAt,
        match.player2AnsweredAt
    ) {
        if (match.phase == "playing" && myPlayer == 1 && (bothAnswered || answerTimeExpired)) {
            finishKoZnaZnaQuestion(matchId, match)
        }
    }

    LaunchedEffect(match.phase, match.questionIndex) {
        if (match.phase == "result" && myPlayer == 1) {
            delay(1500)
            advanceKoZnaZnaQuestion(matchId, match)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            PlayerCircle(
                label = "P1",
                color = Color(0xFF3F51B5),
                isMe = myPlayer == 1
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Ko zna zna",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            PlayerCircle(
                label = "P2",
                color = Color(0xFFE91E63),
                isMe = myPlayer == 2
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Poeni P1: ${match.player1Score}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Poeni P2: ${match.player2Score}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Preostalo vreme: $questionTime s",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pitanje ${match.questionIndex + 1}/$TOTAL_QUESTIONS",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = question.question,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!showAnswers && match.phase == "playing") {
            Text("Odgovori se prikazuju za 3 sekunde...")
            return@Column
        }

        if (match.message.isNotEmpty()) {
            Text(
                text = match.message,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        question.answers.forEach { answer ->

            val buttonColor = when {
                match.phase == "result" && answer == question.correctAnswer -> Color.Green
                match.phase == "result" && answer == myAnswer && myAnswer != question.correctAnswer -> Color.Red
                hasMyAnswered && answer == myAnswer -> Color.Gray
                else -> MaterialTheme.colorScheme.primary
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = match.phase == "playing" && showAnswers && !hasMyAnswered && questionTime > 0,
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                onClick = {
                    val answeredAt = System.currentTimeMillis()

                    if (myPlayer == 1) {
                        MatchRepository.updateMatch(
                            matchId,
                            mapOf(
                                "player1Answer" to answer,
                                "player1AnsweredAt" to answeredAt
                            )
                        )
                    } else {
                        MatchRepository.updateMatch(
                            matchId,
                            mapOf(
                                "player2Answer" to answer,
                                "player2AnsweredAt" to answeredAt
                            )
                        )
                    }
                }
            ) {
                Text(answer)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        if (hasMyAnswered && match.phase == "playing") {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Odgovor je poslat. Čekate protivnika ili istek vremena...")
        }
    }
}

@Composable
private fun PlayerCircle(
    label: String,
    color: Color,
    isMe: Boolean
) {
    Box(
        modifier = Modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isMe) 5.dp else 3.dp,
                color = if (isMe) Color.Green else MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White)
    }
}

private fun finishKoZnaZnaQuestion(
    matchId: String,
    match: OnlineMatch
) {
    if (match.phase != "playing") return

    val question = GameData.triviaQuestions[
        match.questionIndex.coerceAtMost(GameData.triviaQuestions.lastIndex)
    ]

    val p1Answered = match.player1AnsweredAt > 0L
    val p2Answered = match.player2AnsweredAt > 0L

    val p1Correct = p1Answered && match.player1Answer == question.correctAnswer
    val p2Correct = p2Answered && match.player2Answer == question.correctAnswer

    var newP1Score = match.player1Score
    var newP2Score = match.player2Score

    val messageParts = mutableListOf<String>()

    when {
        p1Correct && p2Correct -> {
            if (match.player1AnsweredAt <= match.player2AnsweredAt) {
                newP1Score += 10
                messageParts.add("Oba igrača su odgovorila tačno. Brži je igrač 1. +10")
            } else {
                newP2Score += 10
                messageParts.add("Oba igrača su odgovorila tačno. Brži je igrač 2. +10")
            }
        }

        p1Correct -> {
            newP1Score += 10
            messageParts.add("Igrač 1 je odgovorio tačno. +10")
        }

        p2Correct -> {
            newP2Score += 10
            messageParts.add("Igrač 2 je odgovorio tačno. +10")
        }
    }

    if (p1Answered && !p1Correct) {
        newP1Score -= 5
        messageParts.add("Igrač 1 je odgovorio netačno. -5")
    }

    if (p2Answered && !p2Correct) {
        newP2Score -= 5
        messageParts.add("Igrač 2 je odgovorio netačno. -5")
    }

    if (!p1Answered && !p2Answered) {
        messageParts.add("Niko nije odgovorio.")
    } else {
        if (!p1Answered) messageParts.add("Igrač 1 nije odgovorio.")
        if (!p2Answered) messageParts.add("Igrač 2 nije odgovorio.")
    }

    MatchRepository.updateMatch(
        matchId,
        mapOf(
            "player1Score" to newP1Score,
            "player2Score" to newP2Score,
            "phase" to "result",
            "message" to messageParts.joinToString("\n")
        )
    )
}

private fun advanceKoZnaZnaQuestion(
    matchId: String,
    match: OnlineMatch
) {
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

    MatchRepository.updateMatch(
        matchId,
        mapOf(
            "questionIndex" to nextIndex,
            "phase" to "playing",
            "message" to "",
            "roundStartedAt" to System.currentTimeMillis(),

            "player1Answer" to "",
            "player2Answer" to "",
            "player1AnsweredAt" to 0L,
            "player2AnsweredAt" to 0L,

            "currentPlayer" to 1
        )
    )
}

private data class PlayerAnswer(
    val player: Int,
    val answer: String,
    val answeredAt: Long
)