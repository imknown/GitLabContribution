package net.imknown.gitlabcontribution

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gitlabcontribution.composeapp.generated.resources.Res
import gitlabcontribution.composeapp.generated.resources.compose_multiplatform
import kotlinx.coroutines.launch
import net.imknown.gitlabcontribution.gitlab.makeText
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    GitLab()
}

@Composable
@Preview
fun App0() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

@Composable
fun GitLab() {
    val scope = rememberCoroutineScope()
    var greeting by remember { mutableStateOf("Loading") }

    LaunchedEffect(true) {
        scope.launch {
            greeting = try {
                makeText()
            } catch (e: Exception) {
                e.message ?: "error"
            }
        }
    }

    MaterialTheme {
        Text(
            text = greeting,
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize()
                .padding(4.dp)
                .verticalScroll(rememberScrollState())
        )
    }
}

/** CLI */
private suspend fun main() {
    println(makeText())
}