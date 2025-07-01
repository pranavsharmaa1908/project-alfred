package com.example.alfred_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
import java.io.File
import com.example.alfred_mobile.ui.theme.AlfredmobileTheme
import de.kherud.llama.InferenceParameters
import de.kherud.llama.LlamaModel
import de.kherud.llama.LlamaOutput
import de.kherud.llama.ModelParameters
import de.kherud.llama.args.MiroStat
import android.util.Log



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "Starting onCreate")

        val modelAssetPath = "models/gemma-2-2b-it-IQ3_M.gguf"
        val modelFile = File(cacheDir, "gemma-2-2b-it-IQ3_M.gguf")

        copyAssetModelOnce(this, modelAssetPath, modelFile)

        Log.d("MainActivity", "Model loaded on cache")

        val modelPath = modelFile.absolutePath

        val threads = maxOf(1, Runtime.getRuntime().availableProcessors() - 1)

        val modelParams = ModelParameters().apply {
            setModel(modelPath)
            setThreads(threads)
            Log.d("MainActivity", "Using $threads threads")
        }


        val prompt = "User: answer in 5 words, describe moon."

        Log.d("MainActivity", "prompt received")

        val inferParams = InferenceParameters(prompt)
            .setTemperature(0.5f)
            .setMiroStat(MiroStat.V2)
            .setPenalizeNl(true)
            .setStopStrings("User:")

        Log.d("MainActivity", "inference set")

        val outputText = StringBuilder()

        Log.d("MainActivity", "stringbuilder loaded, starting model...")

        LlamaModel(modelParams).use { model ->
            for (output in model.generate(inferParams)) {
                outputText.append(output)
                Log.d("MainActivity", output.text)
            }
        }



        val finalOutput = outputText.toString()

       
        enableEdgeToEdge()
        setContent {
            AlfredmobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = finalOutput,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlfredmobileTheme {
        Greeting("Android")
    }
}

fun copyAssetModelOnce(context: Context, assetName: String, destFile: File) {
    if (!destFile.exists()) {
        context.assets.open(assetName).use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
