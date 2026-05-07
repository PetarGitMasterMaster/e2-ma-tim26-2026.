package com.example.mobilneprojekat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilneprojekat.ui.screens.KorakPoKorakScreen
import com.example.mobilneprojekat.ui.screens.LoginScreen
import com.example.mobilneprojekat.ui.screens.MojBrojScreen
import com.example.mobilneprojekat.ui.screens.RegisterScreen
import com.example.mobilneprojekat.ui.screens.SelectScreen

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
    }
}