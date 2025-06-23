package com.example.main

import com.example.calendar.Event
import com.example.calendar.Task
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.sql.DriverManager

fun main(){

  val dbConn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db") 

  dbConn.createStatement().execute("""
    CREATE TABLE IF NOT EXISTS events (
      name TEXT NOT NULL,
      date TEXT NOT NULL, 
      time TEXT NOT NULL,
      datetime TEXT NOT NULL
    )
  """.trimIndent())

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

  println(event1)
  println(task1)
}
