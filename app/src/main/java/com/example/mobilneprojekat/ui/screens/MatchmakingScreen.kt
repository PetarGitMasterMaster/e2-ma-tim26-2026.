package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilneprojekat.data.repository.MatchRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

@Composable
fun MatchmakingScreen(
    navController: NavController,
    gameType: String
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var status by remember { mutableStateOf("Traženje protivnika...") }
    var matchId by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var listener by remember { mutableStateOf<ListenerRegistration?>(null) }

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(gameType) {
        if (uid == null) {
            error = "Morate biti prijavljeni"
            return@LaunchedEffect
        }

        MatchRepository.findOrCreateMatch(
            gameType = gameType,
            uid = uid,
            onMatchId = { id ->
                matchId = id
                status = "Čekanje protivnika..."
            },
            onError = { msg ->
                error = msg
            }
        )
    }

    LaunchedEffect(matchId) {
        if (matchId.isEmpty() || uid == null) return@LaunchedEffect

        listener?.remove()
        listener = MatchRepository.listenToMatch(matchId) { match ->
            when (match.status) {
                "active" -> {
                    if (!navigated) {
                        navigated = true
                        status = "Protivnik pronađen! Pokretanje igre..."
                        navController.navigate("onlineGame/$matchId") {
                            popUpTo("select") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
                "finished" -> status = "Meč je završen"
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { listener?.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (matchId.isNotEmpty() && uid != null) {
                    MatchRepository.cancelMatch(matchId, uid)
                }
                navController.popBackStack()
            }
        ) {
            Text("Otkaži")
        }
    }
}
