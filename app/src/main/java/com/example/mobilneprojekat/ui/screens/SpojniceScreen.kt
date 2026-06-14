package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.mobilneprojekat.data.model.SpojniceSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SpojniceScreen(navController: NavController) {

    var round by remember { mutableIntStateOf(1) }

    var currentPlayer by remember { mutableIntStateOf(1) }

    var player1Score by remember { mutableStateOf(0) }
    var player2Score by remember { mutableStateOf(0) }

    var seconds by remember { mutableStateOf(30) }
    var isLoaded by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()

    var allSets by remember { mutableStateOf<List<SpojniceSet>>(emptyList()) }

    var correctPairs by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    var currentSet by remember { mutableStateOf<SpojniceSet?>(null) }

    val leftItems by remember(correctPairs) {
        mutableStateOf(correctPairs.map { it.first }.shuffled())
    }

    val rightItems by remember(correctPairs) {
        mutableStateOf(correctPairs.map { it.second }.shuffled())
    }

    val playerColors = listOf(
        Color(0xFF3F51B5),
        Color(0xFFE91E63)
    )

    var currentIndex by remember { mutableIntStateOf(0) }

    var matches by remember {
        mutableStateOf(listOf<Triple<String, String, Color>>())
    }

    var matchOwners by remember {
        mutableStateOf(mapOf<String, Int>())
    }

    var secondChance by remember { mutableStateOf(false) }

    val currentLeft =
        if (currentIndex < leftItems.size) leftItems[currentIndex]
        else null

    Spacer(modifier = Modifier.height(8.dp))

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("spojnice_sets")
            .get()
            .addOnSuccessListener { snapshot ->
                allSets = snapshot.documents.mapNotNull {
                    it.toObject(SpojniceSet::class.java)
                }

                currentSet = allSets.randomOrNull()
                correctPairs =
                    currentSet?.pairs?.map { it.first to it.second } ?: emptyList()

                round = 1
                currentPlayer = 1
                player1Score = 0
                player2Score = 0
                seconds = 30
                currentIndex = 0
                matches = emptyList()
                secondChance = false

                isLoaded = true
            }
    }

    LaunchedEffect(currentPlayer, round, secondChance, currentIndex) {
        if (currentIndex >= leftItems.size && round <= 2) {

            if (!secondChance) {

                secondChance = true

                currentPlayer = if (currentPlayer == 1) 2 else 1

                currentIndex =
                    leftItems.indexOfFirst { left ->
                        matches.none { it.first == left }
                    }

                if (currentIndex == -1) {
                    currentIndex = leftItems.size
                }

            } else {
                round++
                currentSet = allSets.randomOrNull()
                correctPairs =
                    currentSet?.pairs?.map { it.first to it.second } ?: emptyList()

                secondChance = false
                seconds = 30
                currentPlayer = 2
                matches = emptyList()
                currentIndex = 0
            }

            return@LaunchedEffect
        }

        while (seconds > 0 && currentIndex < leftItems.size) {
            delay(1000)
            seconds--
        }

        return@LaunchedEffect
    }

    if (!isLoaded || correctPairs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading...")
        }
        return
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
                        if (currentPlayer == 1) Color.Green
                        else Color(0xFF3F51B5)
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

            if (round <= 2) {
                Text(
                    text = "Na potezu: Igrač $currentPlayer",
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Text(
                    text = "Kraj Igre",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPlayer == 2) Color.Green
                        else Color(0xFFE91E63)
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
                text = "Poeni P1: $player1Score",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Poeni P2: $player2Score",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (round > 2) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Button(
                    onClick = { navController.navigate("select") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kraj igre")
                }
            }

        } else {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Preostalo vreme: $seconds s",
                    style = MaterialTheme.typography.titleLarge
                )
            }

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

                    leftItems.forEachIndexed { index, item ->

                        val isActive = index == currentIndex
                        val match = matches.find { it.first == item }

                        val color = when {
                            match != null -> match.third
                            isActive -> MaterialTheme.colorScheme.primary
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

                    rightItems.forEach { right ->

                        val match = matches.find { it.second == right }

                        val color = when {
                            match != null -> match.third
                            else -> Color(0xFFF5F5F5)
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

                                        if (currentPlayer == 1) player1Score += 2
                                        else player2Score += 2

                                        matches =
                                            matches + Triple(
                                                currentLeft!!,
                                                right,
                                                playerColors[currentPlayer - 1]
                                            )
                                    }

                                    currentIndex++

                                    if (secondChance) {
                                        while (matches.any {
                                                currentIndex < 5 &&
                                                        it.first == leftItems[currentIndex]
                                            }) {
                                            currentIndex++
                                        }
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
        }
    }
}