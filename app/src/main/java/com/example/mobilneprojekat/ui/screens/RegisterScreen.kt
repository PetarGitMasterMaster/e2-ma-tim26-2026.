package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Registracija",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Korisničko ime") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = region,
            onValueChange = { region = it },
            label = { Text("Region") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Lozinka") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = repeatPassword,
            onValueChange = { repeatPassword = it },
            label = { Text("Ponovi lozinku") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (
                    email.isBlank() ||
                    username.isBlank() ||
                    region.isBlank() ||
                    password.isBlank() ||
                    repeatPassword.isBlank()
                ) {
                    error = "Popuni sva polja"
                } else if (password != repeatPassword) {
                    error = "Lozinke se ne poklapaju"
                } else {
                    error = ""
                    FirebaseFirestore.getInstance()
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                    val user = hashMapOf(
                        "username" to username,
                        "email" to email,
                        "tokens" to 0,
                        "stars" to 0,
                        "league" to "Nista",
                        "region" to region,
                        "password" to password,
                        "avatarColor" to 0xFF3F51B5,
                        "stats" to mapOf(
                            "koZnaZna" to 0,
                            "mojBroj" to 0,
                            "korakPoKorak" to 0,
                            "asocijacije" to 0,
                            "skocko" to 0,
                            "spojnica" to 0
                        ),
                        "totalGames" to 0,
                        "winRate" to 0,
                        "lossRate" to 0
                    )

                    db.collection("users")
                        .document(email)
                        .set(user)

                    navController.navigate("login")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registruj se")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nazad na login")
        }
    }
}