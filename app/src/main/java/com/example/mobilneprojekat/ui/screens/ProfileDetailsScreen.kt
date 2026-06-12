package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.google.firebase.firestore.FirebaseFirestore
import com.example.mobilneprojekat.data.model.UserProfile

@Composable
fun ProfileDetailsScreen(navController: NavController) {

    val db = remember { FirebaseFirestore.getInstance() }

    var userProfile by remember {
        mutableStateOf(UserProfile())
    }

    var avatarColor by remember {
        mutableStateOf(Color(0xFF3F51B5))
    }

    // FETCH FIRESTORE USER
    LaunchedEffect(Unit) {
        db.collection("users")
            .document("testUser")
            .get()
            .addOnSuccessListener { doc ->
                val data = doc.toObject(UserProfile::class.java)
                if (data != null) {
                    userProfile = data
                    avatarColor = Color(data.avatarColor)
                }
            }
    }

    val statistics = userProfile.stats.toList().ifEmpty {
        listOf(
            "Ko zna zna" to 0,
            "Moj broj" to 0,
            "Korak po korak" to 0,
            "Asocijacije" to 0,
            "Skočko" to 0,
            "Spojnice" to 0
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Text(
                text = "Profil korisnika",
                style = MaterialTheme.typography.headlineLarge
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                            .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable {
                                avatarColor =
                                    if (avatarColor == Color(0xFF3F51B5))
                                        Color(0xFFE91E63)
                                    else
                                        Color(0xFF3F51B5)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Avatar", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userProfile.username,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = userProfile.email,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Broj tokena")
                        Text("${userProfile.tokens}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ukupan broj zvezda")
                        Text("${userProfile.stars}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Liga")
                        Text(userProfile.league)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Region")
                        Text(userProfile.region)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "QR kod za prijatelje",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("QR")
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "Statistika igrača",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    statistics.forEach { (name, value) ->

                        Text(name, fontWeight = FontWeight.SemiBold)

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { value / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("$value%")

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ukupan broj partija")
                        Text("${userProfile.totalGames}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pobede")
                        Text("${userProfile.winRate}%")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Porazi")
                        Text("${userProfile.lossRate}%")
                    }
                }
            }
        }

        item {

            Button(
                onClick = { navController.navigate("select") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nazad")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}