package ru.yarsu

import java.util.UUID
import java.time.LocalDateTime

class TaskModel(
    var id: UUID,
    var title: String,
    var registrationDateTime: LocalDateTime,
    var startDateTime: LocalDateTime,
    var endDateTime: LocalDateTime,
    var importance: Importance,
    var urgency: Boolean,
    var percentage: Int,
    var description: String
) {

}
