package com.example.mobilneprojekat.data.model

data class UserProfile(
    val username: String = "",
    val email: String = "",
    val tokens: Int = 0,
    val stars: Int = 0,
    val league: String = "",
    val region: String = "",
    val avatarColor: Long = 0xFF3F51B5,
    val stats: Map<String, Int> = emptyMap(),
    val totalGames: Int = 0,
    val winRate: Int = 0,
    val lossRate: Int = 0,
    var password: String = "",
    var isEmailVerified: Boolean = false
)