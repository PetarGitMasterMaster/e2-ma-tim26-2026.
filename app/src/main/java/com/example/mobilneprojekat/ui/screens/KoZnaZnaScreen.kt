package com.example.mobilneprojekat.ui.screens

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
import kotlinx.coroutines.delay

@Composable
fun KoZnaZnaScreen(navController: NavController) {

    data class Question(
        val question: String,
        val answers: List<String>,
        val correctAnswer: String
    )

    val questions = listOf(
        Question("Glavni grad Srbije?", listOf("Beograd", "Novi Sad", "Niš", "Kragujevac"), "Beograd"),
        Question("Koliko je 5 + 5?", listOf("8", "9", "10", "11"), "10"),
        Question("Najveća planeta?", listOf("Mars", "Jupiter", "Zemlja", "Venera"), "Jupiter"),
        Question("Plava + žuta?", listOf("Crvena", "Zelena", "Narandžasta", "Ljubičasta"), "Zelena"),
        Question("Kontinenti?", listOf("5", "6", "7", "8"), "7")
    )

    var index by remember { mutableIntStateOf(0) }

    var scoreP1 by remember { mutableIntStateOf(0) }
    var scoreP2 by remember { mutableIntStateOf(0) }

    var questionTime by remember { mutableIntStateOf(5) }

    var answered by remember { mutableStateOf(false) }

    var p1Time by remember { mutableStateOf<Long?>(null) }
    var p2Time by remember { mutableStateOf<Long?>(null) }

    var showAnswers by remember { mutableStateOf<Boolean>(false) }

    val q = questions[index]
    var startTime by remember { mutableStateOf(0L) }
    var gameFinished by remember { mutableStateOf(false) }

    // QUESTION TIMER 5s
    LaunchedEffect(index, gameFinished) {
        if(gameFinished)
        {
            return@LaunchedEffect
        }
        questionTime = 5
        answered = false
        p1Time = null
        p2Time = null

        showAnswers = false
        delay(3000)
        showAnswers = true
        startTime = System.currentTimeMillis()

        while (questionTime > 0 && !answered) {
            delay(1000)
            questionTime--
        }

        if (!answered && index < questions.lastIndex) {
            index++
            return@LaunchedEffect
        }

        if (index == questions.lastIndex && answered) {
            gameFinished = true
        }

        if (index == questions.lastIndex && questionTime <= 0) {
            gameFinished = true
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

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFF3F51B5)
                    )
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("P1", color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (index <= questions.lastIndex) "Ko zna zna" else "Kraj Igre",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFE91E63)
                    )
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("P2", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Poeni P1: $scoreP1",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Poeni P2: $scoreP2",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Preostalo vreme: $questionTime s",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // =================================================

        Spacer(modifier = Modifier.height(24.dp))
        if(!gameFinished) Text(q.question)

        if (!gameFinished && index <= questions.lastIndex && showAnswers) {

            Spacer(modifier = Modifier.height(16.dp))

            q.answers.forEach { ans ->

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        if (answered) return@Button

                        val now = System.currentTimeMillis()

                        if (p1Time == null) p1Time = now - startTime
                        if (p2Time == null) p2Time = 3000L

                        answered = true

                        val p1Correct = ans == q.correctAnswer

                        if (p1Correct) {
                            if (p1Time!! < 3000L) scoreP1 += 10
                            else scoreP2 += 10
                        } else {
                            scoreP1 -= 5
                        }

                        if (index < questions.lastIndex) index++
                        else gameFinished = true
                    }
                ) {
                    Text(ans)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

        } else if (index > questions.lastIndex || gameFinished) {
            Button(
                onClick = { navController.navigate("select") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kraj igre")
            }
        }
    }
}