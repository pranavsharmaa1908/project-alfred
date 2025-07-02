package com.example.alfred_mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope            // coroutines scope for Activity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.alfred_mobile.ui.theme.AlfredmobileTheme
import de.kherud.llama.*
import de.kherud.llama.args.MiroStat

import java.io.File

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val threads = maxOf(1, Runtime.getRuntime().availableProcessors() - 1)
    val modelAssetPath = "models/tinyllama-1.1b-chat-v1.0.Q2_K.gguf"
    val modelFile = File(cacheDir, "tinyllama-1.1b-chat-v1.0.Q2_K.gguf")
    copyAssetModelOnce(this, modelAssetPath, modelFile)
    val modelPath = modelFile.absolutePath

    setContent {
      AlfredmobileTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          AppContent(modelPath, threads)
        }
      }
    }
  }
}

@Composable
fun AppContent(ModelPath: String, Threads: Int) {
  var promptState by remember { mutableStateOf("") }
  var outputState = remember { mutableStateOf("") }

  Column(modifier = Modifier
  .fillMaxSize()
  .padding(16.dp)) {

    OutlinedTextField(
      value = promptState,
      onValueChange = {
        promptState = it
        Log.d("MainActivity", "Prompt received: $it")
      },
      label = { Text("Enter Prompt") },
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    ButtonWithCoroutine(
      outputVar = outputState,
      modelPath = ModelPath,
      prompt = promptState,
      threads = Threads,
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(text = outputState.value, modifier = Modifier.fillMaxWidth())
  }
}

fun copyAssetModelOnce(context: android.content.Context, assetName: String, destFile: File) {
  if (!destFile.exists()) {
    context.assets.open(assetName).use { input ->
      destFile.outputStream().use { output ->
        input.copyTo(output)
      }
    }
  }
}


@Composable
fun ButtonWithCoroutine(
  outputVar: MutableState<String>,
  modelPath: String,
  prompt: String,
  threads: Int,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()

  Button(
    onClick = {
      scope.launch(Dispatchers.Default) {
        Log.d("MainActivity", "Button clicked")
        Log.d("MainActivity", "Using $threads threads")

        val modelParams = ModelParameters().apply {
          setModel(modelPath)
          setThreads(threads)
        }

        val inferParams = InferenceParameters(prompt)
        .setTemperature(0.5f)
        .setMiroStat(MiroStat.V2)
        .setPenalizeNl(true)
        .setStopStrings("User:")

        val outputText = StringBuilder()

        LlamaModel(modelParams).use { model ->
          for (output in model.generate(inferParams)) {
            outputText.append(output)
            Log.d("MainActivity", output.text)
          }
        }

        // Switch to main thread to update UI
        withContext(Dispatchers.Main) {
          outputVar.value = outputText.toString()
        }
      }
    },
    modifier = modifier.fillMaxWidth()
  ) {
    Text("Generate")
  }
}

