package ru.yarsu.taskworkflow

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class TaskModel(

    @JsonProperty("ID")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("RegistrationDateTime")
    val registrationDateTime: String,

    @JsonProperty("StartDateTime")
    val startDateTime: String,

    @JsonProperty("EndDateTime")
    val endDateTime: String?,

    @JsonProperty("Importance")
    val importance: Importance,

    @JsonProperty("Urgency")
    val urgency: Boolean,

    @JsonProperty("Percentage")
    val percentage: Int,

    @JsonProperty("Description")
    val description: String
)
//Other views
data class Tasks(
    @JsonProperty("ID")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("IsClosed")
    val isClosed: Boolean

)

data class TaskList(
    @JsonProperty("tasks")
    val tasks: List<Tasks>
)

data class Task(
    @JsonProperty("task-id")
    val id: UUID,

    @JsonProperty("task")
    val task: TaskModel
)

data class ListImportance(
    @JsonProperty("important")
    val important: Boolean?,

    @JsonProperty("urgent")
    val urgent: Boolean?,

    @JsonProperty("tasks")
    val tasks: List<TaskForListImportance>
)

data class TaskForListImportance(
    @JsonProperty("Id")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("Importance")
    val importance: Importance,

    @JsonProperty("Urgency")
    val urgency: Boolean,

    @JsonProperty("Percentage")
    val percentage: Int,

)

