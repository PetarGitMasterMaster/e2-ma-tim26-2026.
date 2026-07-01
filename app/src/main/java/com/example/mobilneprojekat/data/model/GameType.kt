package com.example.mobilneprojekat.data.model

enum class GameType(val route: String, val displayName: String) {
    KORAK("korak", "Korak po korak"),
    MOJBROJ("mojbroj", "Moj broj"),
    KOZNAZNA("koznazna", "Ko zna zna"),
    SPOJNICE("spojnice", "Spojnice");

    companion object {
        fun fromRoute(route: String): GameType? =
            entries.find { it.route == route }
    }
}
