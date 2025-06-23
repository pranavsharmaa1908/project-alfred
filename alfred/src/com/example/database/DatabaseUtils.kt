package com.example.database

import com.example.calendar.Event
import com.example.calendar.Task
import java.sql.DriverManager

val dbConn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db") 

fun CreateDB(){

  dbConn.createStatement().execute("""
  CREATE TABLE IF NOT EXISTS events (
    name TEXT NOT NULL,
    date TEXT NOT NULL, 
    time TEXT NOT NULL,
    datetime TEXT NOT NULL
  )
  """.trimIndent())

  dbConn.createStatement().execute("""
  CREATE TABLE IF NOT EXISTS tasks (
    name TEXT NOT NULL,
    duedate TEXT NOT NULL
  )
  """.trimIndent())
}

fun AddEvents(event: Event){
  val insert = dbConn.prepareStatement("INSERT INTO events (name, date, time, datetime) VALUES (?, ?, ?, ?)")
  insert.setString(1, event.name)
  insert.setString(2, event.date.toString())       // "YYYY-MM-DD"
  insert.setString(3, event.time.toString())       // "HH:mm:ss"
  insert.setString(4, event.datetime.toString())   // "YYYY-MM-DDTHH:mm:ss"
  insert.executeUpdate()
}

fun AddTasks(task: Task){
  val insert = dbConn.prepareStatement("INSERT INTO tasks (name, duedate) VALUES (?, ?)")
  insert.setString(1, task.name)
  insert.setString(2, task.duedate.toString())
  insert.executeUpdate()
}
