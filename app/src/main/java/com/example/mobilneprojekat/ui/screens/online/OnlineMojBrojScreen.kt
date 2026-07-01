package com.example.mobilneprojekat.ui.screens.online

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilneprojekat.data.model.OnlineMatch
import com.example.mobilneprojekat.data.repository.MatchRepository
import kotlinx.coroutines.delay
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.abs

private const val MOJ_BROJ_TIME = 60
private const val MOJ_BROJ_ROUNDS = 3

@Composable
fun OnlineMojBrojScreen(
    navController: NavController,
    matchId: String,
    match: OnlineMatch,
    uid: String
) {
    val myPlayer = match.playerNumber(uid) ?: return
    val isMyTurn = match.isMyTurn(uid)
    var izraz by remember(match.roundStartedAt, match.currentPlayer) { mutableStateOf("") }
    var localSeconds by remember(match.roundStartedAt, match.currentPlayer) {
        mutableIntStateOf(MOJ_BROJ_TIME)
    }

    LaunchedEffect(match.roundStartedAt, match.currentPlayer) {
        localSeconds = MOJ_BROJ_TIME
        while (localSeconds > 0) {
            delay(1000)
            localSeconds--
        }
        if (isMyTurn) {
            submitResult(matchId, match, myPlayer, 0, timedOut = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OnlinePlayerHeader(match, myPlayer, isMyTurn)

        Spacer(modifier = Modifier.height(12.dp))
        Text("P1: ${match.player1Score} (${match.player1Result})  |  P2: ${match.player2Score} (${match.player2Result})")
        Text("Runda ${match.round}/$MOJ_BROJ_ROUNDS")
        Text("Preostalo vreme: $localSeconds s")

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = match.targetNumber?.toString() ?: "?",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            match.generatedNumbers.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { broj ->
                        Button(
                            onClick = {
                                if (isMyTurn) appendNumber(izraz, broj) { izraz = it }
                            },
                            enabled = isMyTurn,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(broj.toString())
                        }
                    }
                    repeat(3 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("+", "-", "*", "/", "(", ")").forEach { op ->
                OutlinedButton(
                    onClick = { if (isMyTurn) izraz = appendOp(izraz, op) },
                    enabled = isMyTurn,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(op)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = izraz,
            onValueChange = {},
            readOnly = true,
            label = { Text("Izraz") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isMyTurn) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { if (izraz.isNotEmpty()) izraz = izraz.dropLast(1) }) {
                    Text("Obriši")
                }
                Button(
                    onClick = {
                        val result = evaluateExpression(izraz)
                        if (result != null) {
                            submitResult(matchId, match, myPlayer, result, timedOut = false)
                            izraz = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Potvrdi")
                }
            }
        } else {
            Text("Čekate protivnika...")
        }
    }
}

private fun appendNumber(current: String, number: Int, setter: (String) -> Unit) {
    val last = current.lastOrNull()
    if (last == null || last in "+-*/(") {
        setter(current + number)
    }
}

private fun appendOp(current: String, op: String): String {
    val last = current.lastOrNull() ?: return if (op == "(") current + op else current
    if (last in "+-*/" && op != "(") return current
    return current + op
}

private fun evaluateExpression(expr: String): Int? {
    return try {
        ExpressionBuilder(expr).build().evaluate().toInt()
    } catch (_: Exception) {
        null
    }
}

private fun submitResult(
    matchId: String,
    match: OnlineMatch,
    myPlayer: Int,
    result: Int,
    timedOut: Boolean
) {
    val target = match.targetNumber ?: return

    val earned = when {
        result == target -> 10
        abs(target - result) <= 5 -> 7
        abs(target - result) <= 10 -> 5
        result == 0 && timedOut -> 0
        else -> 2
    }

    val p1Score = if (myPlayer == 1) match.player1Score + earned else match.player1Score
    val p2Score = if (myPlayer == 2) match.player2Score + earned else match.player2Score
    val p1Result = if (myPlayer == 1) result else match.player1Result
    val p2Result = if (myPlayer == 2) result else match.player2Result

    val nextPlayer = if (match.currentPlayer == 1) 2 else 1
    var nextRound = match.round
    if (nextPlayer == 1) nextRound++

    if (nextRound > MOJ_BROJ_ROUNDS) {
        val msg = when {
            p1Score > p2Score -> "Igrač 1 pobedio!"
            p2Score > p1Score -> "Igrač 2 pobedio!"
            else -> "Nerešeno!"
        }
        MatchRepository.updateMatch(
            matchId,
            mapOf(
                "player1Score" to p1Score,
                "player2Score" to p2Score,
                "player1Result" to p1Result,
                "player2Result" to p2Result
            )
        )
        MatchRepository.finishMatch(matchId, msg)
        return
    }

    val seed = System.currentTimeMillis()
    val (newTarget, newNumbers) = com.example.mobilneprojekat.data.GameData.generateMojBrojNumbers(seed)

    MatchRepository.updateMatch(
        matchId,
        mapOf(
            "currentPlayer" to nextPlayer,
            "round" to nextRound,
            "player1Score" to p1Score,
            "player2Score" to p2Score,
            "player1Result" to 0,
            "player2Result" to 0,
            "targetNumber" to newTarget,
            "generatedNumbers" to newNumbers,
            "roundStartedAt" to System.currentTimeMillis(),
            "message" to if (timedOut) "Vreme isteklo!" else ""
        )
    )
}
