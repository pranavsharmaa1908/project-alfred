package com.example.main

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

  
  val event1 = Event(
    name = "Project Kickoff",
    date = LocalDate.of(2025, 7, 1),
    time = LocalTime.of(10, 0),
    datetime = LocalDateTime.of(2025, 7, 1, 10, 0)
  )

  val task1 = Task(
    name = "Example",
    duedate = LocalDate.of(2025, 7, 1)
  )

  AddEvents(event1)
  AddTasks(task1)
}
