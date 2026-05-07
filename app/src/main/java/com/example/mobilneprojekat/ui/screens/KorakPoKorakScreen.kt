package com.example.mobilneprojekat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KorakPoKorakScreen() {

    val koraci = listOf(
        "Korak 1",
        "Korak 2",
        "Korak 3",
        "Korak 4",
        "Korak 5",
        "Korak 6",
        "Korak 7"
    )

    var odgovor by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Korak po korak",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Preostalo vreme: 70s",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {

            items(koraci) { korak ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = korak,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = odgovor,
            onValueChange = { odgovor = it },
            label = { Text("Unesi odgovor") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Potvrdi")
        }
    }
}