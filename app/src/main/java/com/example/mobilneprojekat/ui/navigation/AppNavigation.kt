package com.example.mobilneprojekat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilneprojekat.ui.screens.KoZnaZnaScreen
import com.example.mobilneprojekat.ui.screens.KorakPoKorakScreen
import com.example.mobilneprojekat.ui.screens.LoginScreen
import com.example.mobilneprojekat.ui.screens.MojBrojScreen
import com.example.mobilneprojekat.ui.screens.ProfileDetailsScreen
import com.example.mobilneprojekat.ui.screens.RegisterScreen
import com.example.mobilneprojekat.ui.screens.SelectScreen
import com.example.mobilneprojekat.ui.screens.SpojniceScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("select") {
            SelectScreen(navController)
        }

        composable("korak") {
            KorakPoKorakScreen()
        }

        composable("mojbroj") {
            MojBrojScreen()
        }

        composable("profil") {
            ProfileDetailsScreen(navController)
        }

        composable("koznazna") {
            KoZnaZnaScreen(navController)
        }

        composable("spojnice") {
            SpojniceScreen(navController)
        }
    }
}