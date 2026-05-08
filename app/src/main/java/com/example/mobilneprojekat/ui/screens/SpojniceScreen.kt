package com.example.mobilneprojekat.ui.screens

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

@Composable
fun SpojniceScreen(navController: NavController) {

    val correctPairs = listOf(
        "Pas" to "Dog",
        "Mačka" to "Cat",
        "Lav" to "Lion",
        "Vuk" to "Wolf",
        "Lisica" to "Fox"
    )

    val leftItems = remember { correctPairs.map { it.first } }
    val rightItems = remember { correctPairs.map { it.second }.shuffled() }

    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }

    var matches by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    val currentLeft = if (currentIndex < leftItems.size) leftItems[currentIndex] else null

    Spacer(modifier = Modifier.height(8.dp))

    Text("Score: $score")

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "Levi pojmovi",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            leftItems.forEachIndexed { index, item ->

                val isActive = index == currentIndex
                val isMatched = matches.any { it.first == item }

                val color = when {
                    isMatched -> Color(0xFF2196F3) // blue
                    isActive -> Color(0xFFE3F2FD)   // current
                    else -> Color(0xFFF5F5F5)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
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

            Text(
                text = "Desni pojmovi",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            rightItems.forEach { right ->

                val isMatched = matches.any { it.second == right }

                val color = when {
                    isMatched -> Color(0xFF2196F3) // correct match stays blue
                    else -> Color(0xFFC8E6C9)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color)
                        .clickable(enabled = currentLeft != null) {

                            val correctRight =
                                correctPairs.find { it.first == currentLeft }?.second

                            val isCorrect = right == correctRight

                            if (isCorrect) {
                                score += 2
                                matches = matches + (currentLeft!! to right)
                            }

                            currentIndex++
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(right)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (currentIndex >= leftItems.size) {

            Button(
                onClick = { navController.navigate("profil") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kraj igre")
            }
        }
    }
}