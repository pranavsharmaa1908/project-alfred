package com.example.main

import com.example.input.EventForm
import com.example.input.TaskForm
import com.example.calendar.Event
import com.example.calendar.Task
import com.example.database.CreateDB
import com.example.database.AddEvents
import com.example.database.AddTasks
import de.kherud.llama.InferenceParameters
import de.kherud.llama.LlamaModel
import de.kherud.llama.LlamaOutput
import de.kherud.llama.ModelParameters
import de.kherud.llama.args.MiroStat
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.sql.DriverManager
import java.nio.charset.StandardCharsets
import java.io.BufferedReader
import java.io.InputStreamReader

fun main(){

  //CreateDB()
  
  //println("Add Task(1) or event(2): ")

  //val inp = readLine()!!

  //when(inp){
  //  "1" -> TaskForm()
  //  "2" -> EventForm()
  //}
  
  val modelPath = "models/gemma-3-1b-it-Q8_0.gguf"

  val modelParams = ModelParameters()
    
  modelParams.setModel(modelPath)

  val systemPrompt = """
      Your name is Alfred, you're a helpful sophisticated personal Assistant, do your best to help the user.
  """.trimIndent()


  BufferedReader(InputStreamReader(System.`in`, StandardCharsets.UTF_8)).use { reader ->
        LlamaModel(modelParams).use { model ->
            var prompt = "$systemPrompt\n"

            while (true) {
                print("\nUser: ")
                val input = reader.readLine()
                if (input == null || input.lowercase() == "exit") break

                prompt += "\nUser: $input\nAlfred: "

                val inferParams = InferenceParameters(prompt)
                    .setTemperature(0.7f)
                    .setMiroStat(MiroStat.V2)
                    .setPenalizeNl(true)
                    .setStopStrings("User:")

                print("Alfred: ")
                for (output in model.generate(inferParams)) {
                    print(output)
                    prompt += output.toString()
                }
            }
        }
    }
}
