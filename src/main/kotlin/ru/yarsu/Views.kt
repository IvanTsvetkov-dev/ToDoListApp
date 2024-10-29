package ru.yarsu

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Local

//Basic view
data class TaskModel(
    @JsonProperty("ID")
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
    var importance: Importance,

    @JsonProperty("Urgency")
    val urgency: Boolean,

    @JsonProperty("Percentage")
    val percentage: Int,

    @JsonProperty("Description")
    val description: String
)
//    : Comparable<TaskModel> {
//    override fun compareTo(other: TaskModel): Int {
//        if(LocalDateTime.parse(registrationDateTime) == LocalDateTime.parse(other.registrationDateTime)){
//            return 0;
//        }
//        if(LocalDateTime.parse(registrationDateTime) < LocalDateTime.parse(other.registrationDateTime)){
//            return -1;
//        }
//        return 1;
//    }
//}
//Other views

// view's for list command
data class TasksForListCommand(
    @JsonProperty("Id")
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

