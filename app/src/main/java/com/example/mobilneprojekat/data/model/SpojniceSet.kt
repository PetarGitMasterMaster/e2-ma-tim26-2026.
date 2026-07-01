package com.example.mobilneprojekat.data.model

data class SpojniceSet(
    val task: String = "",
    val pairs: List<Spojnica> = emptyList()
)

data class Spojnica(
    val first: String = "",
    val second: String = ""
)