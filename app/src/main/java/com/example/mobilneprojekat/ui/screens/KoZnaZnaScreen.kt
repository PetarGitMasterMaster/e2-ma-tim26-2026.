package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        Question(
            question = "Glavni grad Srbije?",
            answers = listOf(
                "Beograd",
                "Novi Sad",
                "Niš",
                "Kragujevac"
            ),
            correctAnswer = "Beograd"
        ),

        Question(
            question = "Koliko je 5 + 5?",
            answers = listOf(
                "8",
                "9",
                "10",
                "11"
            ),
            correctAnswer = "10"
        ),

        Question(
            question = "Najveća planeta Sunčevog sistema?",
            answers = listOf(
                "Mars",
                "Jupiter",
                "Zemlja",
                "Venera"
            ),
            correctAnswer = "Jupiter"
        ),

        Question(
            question = "Koja boja nastaje mešanjem plave i žute?",
            answers = listOf(
                "Crvena",
                "Zelena",
                "Narandžasta",
                "Ljubičasta"
            ),
            correctAnswer = "Zelena"
        ),

        Question(
            question = "Koliko kontinenata postoji?",
            answers = listOf(
                "5",
                "6",
                "7",
                "8"
            ),
            correctAnswer = "7"
        )
    )

    var currentQuestionIndex by remember {
        mutableStateOf(0)
    }

    var score by remember {
        mutableStateOf(0)
    }

    var selectedAnswer by remember {
        mutableStateOf<String?>(null)
    }

    var answerChecked by remember {
        mutableStateOf(false)
    }

    var gameFinished by remember {
        mutableStateOf(false)
    }

    val currentQuestion = questions[currentQuestionIndex]

    // AUTOMATSKI PRELAZ NA SLEDEĆE PITANJE
    LaunchedEffect(answerChecked) {

        if (answerChecked) {

            delay(1500)

            if (currentQuestionIndex < questions.lastIndex) {

                currentQuestionIndex++
                selectedAnswer = null
                answerChecked = false

            } else {

                gameFinished = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Ko zna zna",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Pitanje ${currentQuestionIndex + 1}/5",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Score: $score",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!gameFinished) {

            Text(
                text = currentQuestion.question,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            currentQuestion.answers.forEach { answer ->

                val buttonColor = when {

                    !answerChecked -> MaterialTheme.colorScheme.primary

                    answer == currentQuestion.correctAnswer -> Color.Green

                    answer == selectedAnswer &&
                            answer != currentQuestion.correctAnswer -> Color.Red

                    else -> MaterialTheme.colorScheme.primary
                }

                Button(
                    onClick = {

                        if (!answerChecked) {

                            selectedAnswer = answer
                            answerChecked = true

                            if (answer == currentQuestion.correctAnswer) {
                                score += 10
                            } else {
                                score -= 5
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(answer)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

        } else {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Igra završena!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Konačan rezultat: $score",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navController.navigate("profil")
                    }
                ) {

                    Text("Nazad na profil")
                }
            }
        }
    }
}