package com.example.mobilneprojekat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobilneprojekat.ui.screens.KoZnaZnaScreen
import com.example.mobilneprojekat.ui.screens.KorakPoKorakScreen
import com.example.mobilneprojekat.ui.screens.LoginScreen
import com.example.mobilneprojekat.ui.screens.MatchmakingScreen
import com.example.mobilneprojekat.ui.screens.MojBrojScreen
import com.example.mobilneprojekat.ui.screens.OnlineGameScreen
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
            KorakPoKorakScreen(navController)
        }

        composable("mojbroj") {
            MojBrojScreen(navController)
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

        composable(
            route = "matchmake/{gameType}",
            arguments = listOf(navArgument("gameType") { type = NavType.StringType })
        ) { backStackEntry ->
            val gameType = backStackEntry.arguments?.getString("gameType") ?: "korak"
            MatchmakingScreen(navController, gameType)
        }

        composable(
            route = "onlineGame/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            OnlineGameScreen(navController, matchId)
        }
    }
}
