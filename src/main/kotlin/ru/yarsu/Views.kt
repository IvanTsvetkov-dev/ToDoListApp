package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

//Basic view
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

// view's for list command
data class TasksForListCommand(
    @JsonProperty("ID")
    val id: UUID,

    @JsonProperty("Title")
    val title: String,

    @JsonProperty("IsClosed")
    val isClosed: Boolean

)

data class TaskCommandList(
    @JsonProperty("tasks")
    val tasks: List<TasksForListCommand>
)

//view for show command
data class ParticularTask(
    @JsonProperty("task-id")
    val id: UUID,

    @JsonProperty("task")
    val task: TaskModel
)

//view's for list-eisenhower
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
    val importance: String,

    @JsonProperty("Urgency")
    val urgency: Boolean,

    @JsonProperty("Percentage")
    val percentage: Int,

    )
//view's for list-time
data class TaskForListTime(
    @JsonProperty("time")
    val time: String,

    @JsonProperty("tasks")
    val tasks: List<TaskForListImportance>

)

