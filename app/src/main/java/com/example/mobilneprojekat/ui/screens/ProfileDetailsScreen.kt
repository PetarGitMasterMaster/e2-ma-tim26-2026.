package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProfileDetailsScreen(navController: NavController) {

    var avatarColor by remember {
        mutableStateOf(Color(0xFF3F51B5))
    }

    val statistics = listOf(
        "Ko zna zna" to 72,
        "Moj broj" to 61,
        "Korak po korak" to 55,
        "Asocijacije" to 80,
        "Skočko" to 47,
        "Spojnice" to 91
    )

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
                            .border(
                                4.dp,
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                            .clickable {

                                avatarColor =
                                    if (avatarColor == Color(0xFF3F51B5))
                                        Color(0xFFE91E63)
                                    else
                                        Color(0xFF3F51B5)
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "Avatar",
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "player1",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "player1@gmail.com",
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Broj tokena")
                        Text("120")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ukupan broj zvezda")
                        Text("54")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Liga")
                        Text("Gold 🏆")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Region")
                        Text("Srbija")
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

                    statistics.forEach {

                        Text(
                            text = it.first,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { it.second / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("${it.second}%")

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Ukupan broj partija")
                        Text("248")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pobede")
                        Text("64%")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Porazi")
                        Text("36%")
                    }
                }
            }
        }

        item {

            Button(
                onClick = {
                    navController.navigate("select")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nazad")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}