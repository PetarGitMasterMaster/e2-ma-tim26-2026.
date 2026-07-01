package com.example.mobilneprojekat.ui.screens.online

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
import com.example.mobilneprojekat.data.GameData
import com.example.mobilneprojekat.data.model.OnlineMatch
import com.example.mobilneprojekat.data.repository.MatchRepository
import kotlinx.coroutines.delay

private const val SPOJNICE_TIME = 30
private const val SPOJNICE_ROUNDS = 2

@Composable
fun OnlineSpojniceScreen(
    navController: NavController,
    matchId: String,
    match: OnlineMatch,
    uid: String
) {
    val myPlayer = match.playerNumber(uid) ?: return
    val isMyTurn = match.isMyTurn(uid)

    val leftItems = if (match.leftOrder.isNotEmpty()) {
        match.leftOrder
    } else {
        GameData.spojnicePairs.map { it.first }
    }

    val rightItems = if (match.rightOrder.isNotEmpty()) {
        match.rightOrder
    } else {
        GameData.spojnicePairs.map { it.second }
    }

    val parsedMatches = parseSpojniceMatches(match.matches)

    val currentLeft = if (match.currentIndex in leftItems.indices) {
        leftItems[match.currentIndex]
    } else {
        null
    }

    var seconds by remember(
        match.roundStartedAt,
        match.currentPlayer,
        match.round,
        match.secondChance
    ) {
        mutableIntStateOf(SPOJNICE_TIME)
    }

    LaunchedEffect(
        match.roundStartedAt,
        match.currentPlayer,
        match.round,
        match.secondChance
    ) {
        seconds = SPOJNICE_TIME

        while (seconds > 0 && match.status == "active") {
            delay(1000)
            seconds--
        }

        if (seconds <= 0 && isMyTurn && match.status == "active") {
            finishSpojniceTurn(
                matchId = matchId,
                match = match,
                leftItems = leftItems,
                encodedMatches = match.matches,
                player1Score = match.player1Score,
                player2Score = match.player2Score
            )
        }
    }

    val playerColors = listOf(
        Color(0xFF3F51B5),
        Color(0xFFE91E63)
    )

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

            SpojnicePlayerCircle(
                label = "P1",
                color = if (match.currentPlayer == 1) Color.Green else Color(0xFF3F51B5)
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = when {
                    match.secondChance -> "Druga šansa: Igrač ${match.currentPlayer}"
                    else -> "Na potezu: Igrač ${match.currentPlayer}"
                },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            SpojnicePlayerCircle(
                label = "P2",
                color = if (match.currentPlayer == 2) Color.Green else Color(0xFFE91E63)
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
            text = "Runda ${match.round}/$SPOJNICE_ROUNDS",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Preostalo vreme: $seconds s",
            style = MaterialTheme.typography.titleLarge
        )

        if (match.message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(match.message, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                leftItems.forEachIndexed { index, item ->

                    val isActive = index == match.currentIndex
                    val foundMatch = parsedMatches.find { it.left == item }

                    val color = when {
                        foundMatch != null -> playerColors[foundMatch.player - 1]
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
                        Text(
                            text = item,
                            color = if (foundMatch != null || isActive) Color.White else Color.Black
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                rightItems.forEach { right ->

                    val foundMatch = parsedMatches.find { it.right == right }

                    val color = when {
                        foundMatch != null -> playerColors[foundMatch.player - 1]
                        else -> Color(0xFFF5F5F5)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color)
                            .clickable(
                                enabled = isMyTurn &&
                                        currentLeft != null &&
                                        foundMatch == null &&
                                        match.status == "active"
                            ) {
                                val correctRight = GameData.spojnicePairs
                                    .find { it.first == currentLeft }
                                    ?.second

                                val isCorrect = right == correctRight

                                val newMatches = match.matches.toMutableList()

                                var newP1Score = match.player1Score
                                var newP2Score = match.player2Score

                                if (isCorrect) {
                                    newMatches.add("$currentLeft|$right|$myPlayer")

                                    if (myPlayer == 1) {
                                        newP1Score += 2
                                    } else {
                                        newP2Score += 2
                                    }
                                }

                                var nextIndex = match.currentIndex + 1

                                if (match.secondChance) {
                                    nextIndex = skipAlreadyMatchedLefts(
                                        leftItems = leftItems,
                                        encodedMatches = newMatches,
                                        startIndex = nextIndex
                                    )
                                }

                                if (nextIndex < leftItems.size) {
                                    MatchRepository.updateMatch(
                                        matchId,
                                        mapOf(
                                            "matches" to newMatches,
                                            "currentIndex" to nextIndex,
                                            "player1Score" to newP1Score,
                                            "player2Score" to newP2Score,
                                            "message" to ""
                                        )
                                    )
                                } else {
                                    finishSpojniceTurn(
                                        matchId = matchId,
                                        match = match,
                                        leftItems = leftItems,
                                        encodedMatches = newMatches,
                                        player1Score = newP1Score,
                                        player2Score = newP2Score
                                    )
                                }
                            }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = right,
                            color = if (foundMatch != null) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isMyTurn) {
            Text("Čekate protivnika...")
        }
    }
}

@Composable
private fun SpojnicePlayerCircle(
    label: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                4.dp,
                MaterialTheme.colorScheme.primary,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White)
    }
}

private fun finishSpojniceTurn(
    matchId: String,
    match: OnlineMatch,
    leftItems: List<String>,
    encodedMatches: List<String>,
    player1Score: Int,
    player2Score: Int
) {
    val firstUnmatchedIndex = leftItems.indexOfFirst { left ->
        encodedMatches.none { encoded ->
            encoded.split("|").firstOrNull() == left
        }
    }

    if (!match.secondChance && firstUnmatchedIndex != -1) {
        val nextPlayer = if (match.currentPlayer == 1) 2 else 1

        MatchRepository.updateMatch(
            matchId,
            mapOf(
                "secondChance" to true,
                "currentPlayer" to nextPlayer,
                "currentIndex" to firstUnmatchedIndex,
                "matches" to encodedMatches,
                "player1Score" to player1Score,
                "player2Score" to player2Score,
                "roundStartedAt" to System.currentTimeMillis(),
                "message" to "Drugi igrač dobija preostale pojmove"
            )
        )
        return
    }

    startNextSpojniceRoundOrFinish(
        matchId = matchId,
        match = match,
        player1Score = player1Score,
        player2Score = player2Score
    )
}

private fun startNextSpojniceRoundOrFinish(
    matchId: String,
    match: OnlineMatch,
    player1Score: Int,
    player2Score: Int
) {
    val nextRound = match.round + 1

    if (nextRound > SPOJNICE_ROUNDS) {
        val msg = when {
            player1Score > player2Score -> "Igrač 1 pobedio!"
            player2Score > player1Score -> "Igrač 2 pobedio!"
            else -> "Nerešeno!"
        }

        MatchRepository.updateMatch(
            matchId,
            mapOf(
                "player1Score" to player1Score,
                "player2Score" to player2Score,
                "message" to msg
            )
        )

        MatchRepository.finishMatch(matchId, msg)
        return
    }

    val seed = System.currentTimeMillis()
    val random = kotlin.random.Random(seed)

    val newLeftOrder = GameData.spojnicePairs
        .map { it.first }
        .shuffled(random)

    val newRightOrder = GameData.spojnicePairs
        .map { it.second }
        .shuffled(random)

    MatchRepository.updateMatch(
        matchId,
        mapOf(
            "round" to nextRound,
            "currentPlayer" to 2,
            "secondChance" to false,
            "currentIndex" to 0,
            "matches" to emptyList<String>(),
            "leftOrder" to newLeftOrder,
            "rightOrder" to newRightOrder,
            "roundStartedAt" to System.currentTimeMillis(),
            "player1Score" to player1Score,
            "player2Score" to player2Score,
            "message" to ""
        )
    )
}

private fun skipAlreadyMatchedLefts(
    leftItems: List<String>,
    encodedMatches: List<String>,
    startIndex: Int
): Int {
    var index = startIndex

    while (
        index < leftItems.size &&
        encodedMatches.any { encoded ->
            encoded.split("|").firstOrNull() == leftItems[index]
        }
    ) {
        index++
    }

    return index
}

private fun parseSpojniceMatches(encodedMatches: List<String>): List<OnlineSpojniceMatch> {
    return encodedMatches.mapNotNull { encoded ->
        val parts = encoded.split("|")

        when (parts.size) {
            2 -> OnlineSpojniceMatch(
                left = parts[0],
                right = parts[1],
                player = 1
            )

            3 -> OnlineSpojniceMatch(
                left = parts[0],
                right = parts[1],
                player = parts[2].toIntOrNull()?.coerceIn(1, 2) ?: 1
            )

            else -> null
        }
    }
}

private data class OnlineSpojniceMatch(
    val left: String,
    val right: String,
    val player: Int
)