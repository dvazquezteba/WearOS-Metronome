package com.diego.metronome.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.diego.metronome.R
import com.diego.metronome.presentation.theme.MetronomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() //pantalla de inicio

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault) //tema por defecto, considerar modificar

        setContent { // UI
            //WearApp("Android")
            MetronomeTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost( // No funcionaba con el NavHost
        navController = navController,
        startDestination = "welcome"
    ) { // aquí las pantallas
        composable("welcome") {
            WelcomeScreen {
                navController.navigate("main") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }
        composable("main") {
            MainScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.title2,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onContinue) {
                Text("Start")
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    var bpm by remember { mutableIntStateOf(120) }
    var isPlaying by remember { mutableStateOf(false) }

    val maxBpm = 240
    val minBpm = 40
    val arcSweepAngle = ((bpm - minBpm).toFloat() / (maxBpm - minBpm)) * 270f + 10f // mínimo ángulo visible

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = size.minDimension * 0.9f
            val topLeft = Offset(
                (this.size.width - size) / 2,
                (this.size.height - size) / 2
            )
            drawArc(
                color = Color.Gray,
                startAngle = 135f,
                sweepAngle = arcSweepAngle,
                useCenter = false,
                style = Stroke(width = 12f, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = Size(size, size)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // BPM Display Box
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "$bpm BPM",
                    style = MaterialTheme.typography.title2,
                    color = MaterialTheme.colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { if (bpm > minBpm) bpm-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Reduce BPM")
                }

                Button(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }

                Button(onClick = { if (bpm < maxBpm) bpm++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase BPM")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Options Button
            Button(onClick = { navController.navigate("settings") }) {
                Text("Settings")
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Settings")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
        }
    }
}

@Composable
fun WearApp(greetingName: String) {
    MetronomeTheme {
        Box( // fondo de la pantalla
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) { // aquí va el código de mi app
            TimeText() // para poner la hora arriba
            Greeting(greetingName = greetingName) // para poner el texto
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

// previews para no usar el emulador/reloj
@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Metronome App!")
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, showBackground = true)
@Composable
fun PreviewWelcomeScreen() {
    MetronomeTheme {
        WelcomeScreen(onContinue = {})
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, showBackground = true)
@Composable
fun PreviewMainScreen() {
    MetronomeTheme {
        MainScreen(navController = rememberSwipeDismissableNavController())
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    MetronomeTheme {
        SettingsScreen(navController = rememberSwipeDismissableNavController())
    }
}
