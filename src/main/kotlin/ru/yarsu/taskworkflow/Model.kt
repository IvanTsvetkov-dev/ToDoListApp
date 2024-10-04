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

