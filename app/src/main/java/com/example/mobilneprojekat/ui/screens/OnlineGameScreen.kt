package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilneprojekat.data.model.GameType
import com.example.mobilneprojekat.data.model.OnlineMatch
import com.example.mobilneprojekat.data.repository.MatchRepository
import com.example.mobilneprojekat.ui.screens.online.OnlineKoZnaZnaScreen
import com.example.mobilneprojekat.ui.screens.online.OnlineKorakPoKorakScreen
import com.example.mobilneprojekat.ui.screens.online.OnlineMojBrojScreen
import com.example.mobilneprojekat.ui.screens.online.OnlineSpojniceScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun OnlineGameScreen(
    navController: NavController,
    matchId: String
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var match by remember { mutableStateOf<OnlineMatch?>(null) }

    DisposableEffect(matchId) {
        val registration = MatchRepository.listenToMatch(matchId) { updated ->
            match = updated
        }
        onDispose { registration.remove() }
    }

    val currentMatch = match

    if (currentMatch == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentMatch.status == "finished") {
        OnlineGameFinished(
            match = currentMatch,
            uid = uid,
            onBack = {
                navController.navigate("select") {
                    popUpTo("select") { inclusive = false }
                }
            }
        )
        return
    }

    when (GameType.fromRoute(currentMatch.gameType)) {
        GameType.KORAK -> OnlineKorakPoKorakScreen(navController, matchId, currentMatch, uid)
        GameType.MOJBROJ -> OnlineMojBrojScreen(navController, matchId, currentMatch, uid)
        GameType.KOZNAZNA -> OnlineKoZnaZnaScreen(navController, matchId, currentMatch, uid)
        GameType.SPOJNICE -> OnlineSpojniceScreen(navController, matchId, currentMatch, uid)
        null -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Nepoznat tip igre")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Nazad")
                }
            }
        }
    }
}

@Composable
private fun OnlineGameFinished(
    match: OnlineMatch,
    uid: String,
    onBack: () -> Unit
) {
    val myPlayer = match.playerNumber(uid)
    val myScore = if (myPlayer == 1) match.player1Score else match.player2Score
    val oppScore = if (myPlayer == 1) match.player2Score else match.player1Score

    val resultText = when {
        myScore > oppScore -> "Pobedili ste!"
        myScore < oppScore -> "Izgubili ste."
        else -> "Nerešeno!"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Meč završen",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = resultText,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Vaš rezultat: $myScore")
        Text("Protivnik: $oppScore")
        if (match.message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(match.message)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) {
            Text("Nazad na izbor igara")
        }
    }
}
