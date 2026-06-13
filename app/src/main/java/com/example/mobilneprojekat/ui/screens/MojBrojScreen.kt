package com.example.mobilneprojekat.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random
import kotlin.math.abs


@Composable
fun MojBrojScreen(navController: NavController) {

    var izraz by remember { mutableStateOf<List<String>>(emptyList()) }

    var rounds by remember { mutableStateOf(1) }

    var currentPlayer by remember { mutableStateOf(1) }

    var targetNumber by remember { mutableStateOf<Int?>(null) }

    var generatedNumbers by remember { mutableStateOf<List<Int>>(emptyList()) }

    var expression by remember { mutableStateOf("") }

    var player1Score by remember { mutableStateOf(0) }
    var player2Score by remember { mutableStateOf(0) }

    var secondsLeft by remember { mutableStateOf(60) }

    var stopPressed by remember { mutableStateOf(false) }

    var player1num by remember { mutableStateOf(0) }
    var player2num by remember { mutableStateOf(0) }

    var seconds by remember { mutableStateOf(60) }
    var autoReveal by remember { mutableStateOf(5) }

    fun generateTarget(){
        targetNumber =  (100..999).random()
    }

    fun generateNumbers(){

        val numbers = mutableListOf<Int>()

        repeat(4) {
            numbers.add((1..9).random())
        }

        numbers.add(listOf(10, 15, 20).random())

        numbers.add(listOf(25, 50, 75, 100).random())

        generatedNumbers = numbers
    }

    fun evaluate(){
        val expr = izraz.joinToString("")

        val result = ExpressionBuilder(expr)
            .build()
            .evaluate()

        var earnedPoints = 10
        if (currentPlayer == 1) {
            player1num = result.toInt()
        } else {
            player2num = result.toInt()
        }

        Log.i("NIGGA", abs(targetNumber!! - result.toInt()!!).toString() + " " + abs(targetNumber!! - player2num).toString())

        if(result.toInt() == targetNumber){
            var earnedPoints = 10
            if (currentPlayer == 1) {
                player1Score += earnedPoints
            } else {
                player2Score += earnedPoints
            }
        }else{
            if (currentPlayer == 1) {
                if (abs(targetNumber!! - player1num) < abs(targetNumber!! - player2num)) {
                    var earnedPoints = 5
                    player1Score += earnedPoints
                }else{
                    var earnedPoints = 5
                    player2Score += earnedPoints
                }
            } else {
                if (abs(targetNumber!! - player2num) < abs(targetNumber!! - player1num)) {
                    var earnedPoints = 5
                    player2Score += earnedPoints
                }else{
                    var earnedPoints = 5
                    player1Score += earnedPoints

                }
            }
            if(abs(targetNumber!! - player1num)== abs(targetNumber!! - player2num)){
                var earnedPoints = 5
                if (currentPlayer == 1) {
                    player1Score += earnedPoints
                } else {
                    player2Score += earnedPoints
                }
            }
        }
        seconds = 0
    }

    LaunchedEffect(targetNumber) {

        if (currentPlayer == 1) {
            player2num = 500
        } else {
            player1num = 500
        }

        seconds = 60
        autoReveal = 5

        while(targetNumber == null && autoReveal > 0){
            delay(1000)
            autoReveal--
            if(autoReveal == 0){
                generateTarget()
            }
        }
        autoReveal = 5
        while(generatedNumbers.isEmpty() && autoReveal > 0){
            delay(1000)
            autoReveal--
            if(autoReveal == 0){
                generateNumbers()
            }
        }

        while (seconds > 0) {
                delay(1000)
                seconds--
        }

        while (seconds > -10) {
            delay(1000)
            seconds--
        }

        currentPlayer =
            if (currentPlayer == 1) 2 else 1

        if(currentPlayer == 1){
            rounds++
        }
        if(rounds == 3){
            navController.navigate("profil")
        }

        targetNumber = null;
        generatedNumbers = emptyList()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPlayer == 1)
                            Color.Green
                        else
                            Color(0xFF3F51B5)
                    )
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("P1", color = Color.White)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Na potezu: Igrač $currentPlayer",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentPlayer == 2)
                            Color.Green
                        else
                            Color(0xFFE91E63)
                    )
                    .border(
                        4.dp,
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("P2", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ){
        Text(
            text = "Poeni P1: $player1Score",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Poeni P2: $player2Score",
            style = MaterialTheme.typography.titleMedium
        )}
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ){
        Text(
            text = "Broj P1: $player1num",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Broj P2: $player2num",
            style = MaterialTheme.typography.titleMedium
        )}
    }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Preostalo vreme: $seconds s",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = targetNumber.toString(),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                generatedNumbers.take(3).forEach { broj ->

                    Button(
                        onClick = {
                            if(!(izraz.lastOrNull() == "+" || izraz.lastOrNull() == "-"
                                        || izraz.lastOrNull() == "*" || izraz.lastOrNull() == "/" || izraz.lastOrNull() == "(")){

                            }else{
                                izraz = izraz + broj.toString()
                            }
                            if(izraz.isEmpty()){
                                izraz = izraz + broj.toString()
                            }

                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = broj.toString(),
                            maxLines = 1
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                generatedNumbers.drop(3).take(3).forEach { broj ->

                    Button(
                        onClick = {
                            if(!(izraz.lastOrNull() == "+" || izraz.lastOrNull() == "-"
                                        || izraz.lastOrNull() == "*" || izraz.lastOrNull() == "/" || izraz.lastOrNull() == "(")){

                            }else{
                                izraz = izraz + broj.toString()
                            }
                            if(izraz.isEmpty()){
                                izraz = izraz + broj.toString()
                            }

                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = broj.toString(),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            listOf("+", "-", "*", "/", "(" , ")").forEach { op ->

                OutlinedButton(
                    onClick = {
                        if((izraz.lastOrNull() == "+" || izraz.lastOrNull() == "-"
                            || izraz.lastOrNull() == "*" || izraz.lastOrNull() == "/") && op != "("){

                        }else{
                            izraz = izraz + op
                        }

                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(op)
                }
            }

        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                if (izraz.isNotEmpty()) {
                    izraz = izraz.dropLast(1)
                }
            },
        ) {
            Text("Delete")
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = izraz.joinToString(""),
            onValueChange = { },
            readOnly = true,
            label = { Text("Izraz") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (targetNumber == null){
            Button(
                onClick = { generateTarget()},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop")
            }
        }else if(generatedNumbers.isEmpty()){
            Button(
                onClick = { generateNumbers()},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop")
            }
        }else{
            Button(
                onClick = { evaluate()},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Potvrdi")
            }
        }
        Button(
            onClick = {
                navController.navigate("profil")
            }
        ) {

            Text("Nazad na profil")
        }
    }
}