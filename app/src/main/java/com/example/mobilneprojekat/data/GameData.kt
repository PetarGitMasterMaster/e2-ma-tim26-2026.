package com.example.mobilneprojekat.data

data class KorakPuzzle(val answer: String, val hints: List<String>)

data class TriviaQuestion(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String
)

object GameData {

    val korakPuzzles = listOf(
        KorakPuzzle(
            answer = "Tesla",
            hints = listOf(
                "Srpski naučnik",
                "Pronalazač",
                "Naizmenična struja",
                "Rođen u Smiljanu",
                "Nikola _____"
            )
        ),
        KorakPuzzle(
            answer = "Beograd",
            hints = listOf(
                "Glavni grad",
                "Nalazi se na dve reke",
                "Kalemegdan",
                "Prestonica Srbije",
                "_____ na vodi"
            )
        ),
        KorakPuzzle(
            answer = "Nikola",
            hints = listOf(
                "Ime srpskog naučnika",
                "Pronalazač",
                "Tesla je prezime",
                "_____ Tesla"
            )
        )
    )

    val triviaQuestions = listOf(
        TriviaQuestion(
            question = "Glavni grad Srbije?",
            answers = listOf("Beograd", "Novi Sad", "Niš", "Kragujevac"),
            correctAnswer = "Beograd"
        ),
        TriviaQuestion(
            question = "Koliko je 5 + 5?",
            answers = listOf("8", "9", "10", "11"),
            correctAnswer = "10"
        ),
        TriviaQuestion(
            question = "Najveća planeta Sunčevog sistema?",
            answers = listOf("Mars", "Jupiter", "Zemlja", "Venera"),
            correctAnswer = "Jupiter"
        ),
        TriviaQuestion(
            question = "Koja boja nastaje mešanjem plave i žute?",
            answers = listOf("Crvena", "Zelena", "Narandžasta", "Ljubičasta"),
            correctAnswer = "Zelena"
        ),
        TriviaQuestion(
            question = "Koliko kontinenata postoji?",
            answers = listOf("5", "6", "7", "8"),
            correctAnswer = "7"
        )
    )

    val spojnicePairs = listOf(
        "Pas" to "Dog",
        "Mačka" to "Cat",
        "Lav" to "Lion",
        "Vuk" to "Wolf",
        "Lisica" to "Fox"
    )

    fun generateMojBrojNumbers(seed: Long): Pair<Int, List<Int>> {
        val random = kotlin.random.Random(seed)
        val target = random.nextInt(100, 1000)
        val numbers = mutableListOf<Int>()
        repeat(4) { numbers.add(random.nextInt(1, 10)) }
        numbers.add(listOf(10, 15, 20)[random.nextInt(3)])
        numbers.add(listOf(25, 50, 75, 100)[random.nextInt(4)])
        return target to numbers
    }
}
