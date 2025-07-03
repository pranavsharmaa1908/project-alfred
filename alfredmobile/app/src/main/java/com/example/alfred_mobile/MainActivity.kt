package com.example.alfred_mobile

// Android core
import android.os.Bundle
import android.util.Log

// AndroidX Activity + Lifecycle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope

// Jetpack Compose - Runtime & UI
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Jetpack Compose - Layout & Scroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


// Jetpack Compose - Material 3
import androidx.compose.material3.*

// Kotlin Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// External Libraries
import de.kherud.llama.*
import de.kherud.llama.args.MiroStat

// Project Packages
import com.example.alfred_mobile.ui.theme.AlfredmobileTheme
import com.example.chatutils.Chats





import java.io.File

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val threads = maxOf(1, Runtime.getRuntime().availableProcessors() - 1)
    val modelAssetPath = "models/gemma-3-1b-it-q4_0.gguf"
    val modelFile = File(cacheDir, "gemma-3-1b-it-q4_0.gguf")
    copyAssetModelOnce(this, modelAssetPath, modelFile)
    val modelPath = modelFile.absolutePath

    val chat = Chats("AI", "Hello")

    Log.d("MainActivity", chat.Text )

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
  var ChatList = remember { mutableStateListOf<Chats>() }

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
      chatlist = ChatList
    )
    Spacer(modifier = Modifier.height(16.dp))
    
    Text(text =outputState.value, modifier = Modifier.fillMaxWidth())
    
    Spacer(modifier = Modifier.height(16.dp))

    LazyColumn(
      modifier = Modifier
      .fillMaxSize()
      .padding(top = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(ChatList) { chat ->
        ChatBubble(chat)
      }
    }

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
  chatlist: SnapshotStateList<Chats>,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()

  Button(
    onClick = {
      chatlist.add(Chats("User", prompt))
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

        val outputText = StringBuilder()

        LlamaModel(modelParams).use { model ->
          for (output in model.generate(inferParams)) {
            outputText.append(output)
            Log.d("MainActivity", output.text)
            withContext(Dispatchers.Main) {
              outputVar.value = outputText.toString()
            }
          }
        }
        withContext(Dispatchers.Main) {
          outputVar.value = outputText.toString()
          chatlist.add(Chats("AI", outputVar.value))
        }
        Log.d("MainActivity", chatlist[0].Text)
      }
    },
    modifier = modifier.fillMaxWidth()
  ) {
    Text("Generate")
  }
}

@Composable
fun ChatBubble(chat: Chats){
  val isUser = chat.Flag == "User"
  val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
  val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary

  Row(
    modifier = Modifier
      .fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    Surface(
      color = bubbleColor,
      shape = MaterialTheme.shapes.medium,
      tonalElevation = 2.dp
    ) {
      Text(
        text = chat.Text,
        modifier = Modifier.padding(12.dp),
        color = textColor
      )
    }
  }
}
