package com.diego.metronome.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.diego.metronome.R
import com.diego.metronome.presentation.theme.MetronomeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

    // Aquí lo creas a nivel de actividad
    val settingsViewModel: SettingsViewModel = viewModel()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen {
                navController.navigate("main") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }
        composable("main") {
            MainScreen(navController = navController, viewModel = settingsViewModel)
        }
        composable("settings") {
            SettingsScreen(navController = navController, viewModel = settingsViewModel)
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally) {
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

class MainViewModel : ViewModel() {
    private val _bpm = MutableStateFlow(120)
    val bpm: StateFlow<Int> = _bpm

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val maxBpm = 240
    private val minBpm = 30

    fun increaseBpm() {
        if (_bpm.value < maxBpm) _bpm.value++
    }

    fun decreaseBpm() {
        if (_bpm.value > minBpm) _bpm.value--
    }

    fun togglePlaying() {
        _isPlaying.value = !_isPlaying.value
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    mainViewModel: MainViewModel = viewModel()
) {
    val bpm by mainViewModel.bpm.collectAsState()
    val isPlaying by mainViewModel.isPlaying.collectAsState()

    val maxBpm = 240
    val minBpm = 30
    val targetSweepAngle = ((bpm - minBpm).toFloat() / (maxBpm - minBpm)) * 228f + 28.5f
    val animatedSweepAngle by animateFloatAsState(
        targetValue = targetSweepAngle,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "ArcSweepAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = size.minDimension * 0.9f
            val topLeft = Offset(
                (this.size.width - size) / 2,
                (this.size.height - size) / 2
            )
            drawArc(
                color = Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f),
                startAngle = 142f,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                style = Stroke(width = 8f, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = Size(size, size)
            )
        }


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Listening",
                    modifier = Modifier.height(12.dp).width(12.dp))
                Icon(Icons.Default.BarChart, contentDescription = "Listening",
                    modifier = Modifier.height(12.dp).width(12.dp))
            }

            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(25.dp))
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "$bpm BPM",
                    style = MaterialTheme.typography.title2,
                    color = MaterialTheme.colors.onBackground
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { mainViewModel.decreaseBpm() },
                    colors = ButtonDefaults.buttonColors(Color.DarkGray.copy(alpha = 0.85f)),
                    modifier = Modifier.width(30.dp).height(30.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Reduce BPM",
                        modifier = Modifier.height(15.dp).width(15.dp),
                        tint = Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f))
                }

                Button(
                    onClick = { mainViewModel.togglePlaying() },
                    colors = ButtonDefaults.buttonColors(
                        Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f)),
                    modifier = Modifier.width(30.dp).height(30.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.height(15.dp).width(15.dp),
                        tint = MaterialTheme.colors.onBackground
                    )
                }

                Button(
                    onClick = { mainViewModel.increaseBpm() },
                    colors = ButtonDefaults.buttonColors(Color.DarkGray.copy(alpha = 0.85f)),
                    modifier = Modifier.width(30.dp).height(30.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase BPM",
                        modifier = Modifier.height(15.dp).width(15.dp),
                        tint = Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f))
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = { navController.navigate("settings") },
                colors = ButtonDefaults.buttonColors(
                    Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f)),
                modifier = Modifier.width(30.dp).height(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.height(13.dp).width(13.dp),
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

class SettingsViewModel : ViewModel() {

    private val _vibrationMode = MutableStateFlow(true)
    val vibrationMode: StateFlow<Boolean> = _vibrationMode

    private val _sliderMode = MutableStateFlow(true)
    val sliderMode: StateFlow<Boolean> = _sliderMode

    private val _aiMode = MutableStateFlow(true)
    val aiMode: StateFlow<Boolean> = _aiMode

    fun toggleVibration() {
        _vibrationMode.value = !_vibrationMode.value
    }

    fun toggleSlider() {
        _sliderMode.value = !_sliderMode.value
    }

    fun toggleAI() {
        _aiMode.value = !_aiMode.value
    }
}

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {

    val vibrationMode by viewModel.vibrationMode.collectAsState()
    val sliderMode by viewModel.sliderMode.collectAsState()
    val aiMode by viewModel.aiMode.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Settings")
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.toggleVibration() },
                colors = if (vibrationMode)
                    ButtonDefaults.buttonColors(Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f))
                else
                    ButtonDefaults.buttonColors(Color.Gray.copy(alpha = 0.75f)),
                modifier = Modifier.width(135.dp).height(30.dp)
            ) {
                Text("Vibration")
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { viewModel.toggleSlider() },
                colors = if (sliderMode)
                    ButtonDefaults.buttonColors(Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f))
                else
                    ButtonDefaults.buttonColors(Color.Gray.copy(alpha = 0.75f)),
                modifier = Modifier.width(135.dp).height(30.dp)
            ) {
                Text("Slider")
            }

            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = { viewModel.toggleAI() },
                colors = if (aiMode)
                    ButtonDefaults.buttonColors(Color.Gray.copy(alpha = 0.75f).copy(blue = 0.75f).copy(red = 0.4f))
                else
                    ButtonDefaults.buttonColors(Color.Gray.copy(alpha = 0.75f)),
                modifier = Modifier.width(135.dp).height(30.dp)
            ) {
                Text("AI corrector")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.width(30.dp).height(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowDown,
                    contentDescription = "Back"
                )
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
/*
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
*/

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, showBackground = true)
@Composable
fun PreviewMainScreen() {
    MetronomeTheme {
        MainScreen(
            navController = rememberSwipeDismissableNavController(),
            viewModel = SettingsViewModel()
        )
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true, showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    MetronomeTheme {
        SettingsScreen(navController = rememberSwipeDismissableNavController())
    }
}
