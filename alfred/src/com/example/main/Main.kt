package com.example.main

import com.example.input.EventForm
import com.example.input.TaskForm
import com.example.calendar.Event
import com.example.calendar.Task
import com.example.database.CreateDB
import com.example.database.AddEvents
import com.example.database.AddTasks
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.sql.DriverManager

fun main(){

  CreateDB()
  
  EventForm()

  TaskForm()
}
