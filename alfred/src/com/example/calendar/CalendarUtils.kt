package com.example.calendar

import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

data class Event(
  var name: String,
  var date: LocalDate,
  var time: LocalTime,
  var datetime: LocalDateTime
)

data class Task(
  var name: String,
  var duedate: LocalDate
)
