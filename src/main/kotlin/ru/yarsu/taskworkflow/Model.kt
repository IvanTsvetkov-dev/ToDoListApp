package ru.yarsu.taskworkflow

import java.util.UUID

data class TaskModel(
    val id: UUID,
    val title: String,
    val registrationDateTime: String,
    val startDateTime: String,
    val endDateTime: String?,
    val importance: Importance,
    val urgency: Boolean,
    val percentage: Int,
    val description: String
)

