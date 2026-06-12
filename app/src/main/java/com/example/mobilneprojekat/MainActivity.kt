package com.example.mobilneprojekat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobilneprojekat.ui.navigation.AppNavigation
import com.example.mobilneprojekat.ui.theme.MobilneProjekatTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //brboc
        FirebaseFirestore.getInstance()
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val user = hashMapOf(
            "username" to "player1",
            "email" to "player1@gmail.com",
            "tokens" to 120,
            "stars" to 54,
            "league" to "Gold 🏆",
            "region" to "Srbija",
            "avatarColor" to 0xFF3F51B5,
            "stats" to mapOf(
                "koZnaZna" to 72,
                "mojBroj" to 61,
                "korakPoKorak" to 55,
                "asocijacije" to 80,
                "skocko" to 47,
                "spojnica" to 91
            ),
            "totalGames" to 248,
            "winRate" to 64,
            "lossRate" to 36
        )

        db.collection("users")
            .document("testUser")   // hardkodovan ID za sada
            .set(user)

        //brboc
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}