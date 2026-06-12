package com.example.mobilneprojekat.ui.screens

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
import kotlinx.coroutines.delay

data class KorakPoKorakPuzzle(
    val answer: String,
    val hints: List<String>
)

@Composable
fun KorakPoKorakScreen(navController: NavController) {

    val puzzles = listOf(
        KorakPoKorakPuzzle(
            answer = "Tesla",
            hints = listOf(
                "Srpski naučnik",
                "Pronalazač",
                "Naizmenična struja",
                "Rođen u Smiljanu",
                "Nikola _____"
            )
        ),
        KorakPoKorakPuzzle(
            answer = "Beograd",
            hints = listOf(
                "Glavni grad",
                "Nalazi se na dve reke",
                "Kalemegdan",
                "Prestonica Srbije",
                "_____ na vodi"
            )
        )
    )



    var currentPuzzleIndex by remember { mutableStateOf(0) }
    var revealedHints by remember { mutableStateOf(1) }
    var answerInput by remember { mutableStateOf("") }

    var player1Score by remember { mutableStateOf(0) }
    var player2Score by remember { mutableStateOf(0) }

    var currentPlayer by remember { mutableStateOf(1) }

    var seconds by remember { mutableStateOf(70) }

    var message by remember { mutableStateOf("") }

    val puzzle = puzzles[currentPuzzleIndex]

    LaunchedEffect(currentPuzzleIndex) {

        seconds = 70

        while (seconds > 0) {
            delay(1000)
            seconds--
            if(seconds%10==0){
                revealedHints++
            }
        }

        message = "Vreme je isteklo!"

        currentPlayer =
            if (currentPlayer == 1) 2 else 3

        while (seconds > -10) {
            delay(1000)
            seconds--
        }

        revealedHints = 1
        answerInput = ""

        currentPuzzleIndex =
            (currentPuzzleIndex + 1) % puzzles.size
    }

    fun nextTurn() {
        if(seconds > 0){
            currentPlayer =
                if (currentPlayer == 1) 2 else 1
        }

        revealedHints = 1
        answerInput = ""

        currentPuzzleIndex =
            (currentPuzzleIndex + 1) % puzzles.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
                        if (currentPlayer == 1)
                            Color.Green
                        else
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
                text = "Na potezu: Igrač $currentPlayer",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPlayer == 2)
                            Color.Green
                        else
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

        Text(
            text = "Poeni P1: $player1Score",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Poeni P2: $player2Score",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Preostalo vreme: $seconds s",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(
                puzzle.hints.take(revealedHints)
            ) { hint ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = hint,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = answerInput,
            onValueChange = {
                answerInput = it
            },
            label = {
                Text("Unesi odgovor")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (
                        answerInput.trim()
                            .equals(
                                puzzle.answer,
                                ignoreCase = true
                            )
                    ) {

                        var earnedPoints =
                            (20 - ((revealedHints-1)*2))

                        if(seconds < 0){
                            earnedPoints = 5;
                        }

                        if (currentPlayer == 1) {
                            player1Score += earnedPoints
                        } else {
                            player2Score += earnedPoints
                        }

                        message =
                            "Tačno! +$earnedPoints poena"

                        nextTurn()
                    }
                    else {
                        message = "Netačan odgovor"
                    }

                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Potvrdi")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary
        )

    }
}