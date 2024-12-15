package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class User(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Login")
    val login: String,
    @JsonProperty("RegistrationDateTime")
    val registrationDateTime: String,
    @JsonProperty("Email")
    val email: String,
)

data class Categories(
    var id: UUID,
    val description: String,
    val color: String,
    val owner: UUID?,
)

data class TaskModel(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Title")
    val title: String,
    @JsonProperty("RegistrationDateTime")
    val registrationDateTime: LocalDateTime,
    @JsonProperty("StartDateTime")
    val startDateTime: LocalDateTime,
    @JsonProperty("EndDateTime")
    val endDateTime: LocalDateTime?,
    @JsonProperty("Importance")
    var importance: String,
    @JsonProperty("Urgency")
    val urgency: Boolean,
    @JsonProperty("Percentage")
    val percentage: Int,
    @JsonProperty("Description")
    val description: String,
    @JsonProperty("Author")
    var author: UUID,
    @JsonProperty("Category")
    val category: UUID,
)

data class TasksForListCommand(
    @JsonProperty("Id")
    val id: UUID,
    @JsonProperty("Title")
    val title: String,
    @JsonProperty("IsClosed")
    val isClosed: Boolean,
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
