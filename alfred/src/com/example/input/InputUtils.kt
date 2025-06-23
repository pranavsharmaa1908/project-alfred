package com.example.input

import com.example.calendar.Event
import com.example.calendar.Task
import com.example.database.AddEvents
import com.example.database.AddTasks
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

fun EventForm(){
  println("Enter the name of the event: ")
  val eventName = readLine()!!
  println("Enter Date(YYYY-MM-DD): ")
  val eventDate = readLine()!!
  println("Enter Time(HH:mm): ")
  val eventTime = readLine()!!
  
  val eventDateTime = eventDate + "T" + eventTime

  val event_buff = Event(
    eventName,
    LocalDate.parse(eventDate),
    LocalTime.parse(eventTime),
    LocalDateTime.parse(eventDateTime)
  )

  AddEvents(event_buff)
}

fun TaskForm(){
  println("Enter the name of the Task: ")
  val taskName = readLine()!!
  println("Enter Due Date(YYYY-MM-DD): ")
  val taskDueDate = readLine()!!

  val task_buff = Task(
    taskName,
    LocalDate.parse(taskDueDate)
  )

  AddTasks(task_buff)
}
