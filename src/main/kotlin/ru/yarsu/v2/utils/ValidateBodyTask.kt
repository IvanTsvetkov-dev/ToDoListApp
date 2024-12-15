package ru.yarsu.v2.utils

import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.UUID

fun validateBody(
    data: Map<String, Any>,
    userList: MutableList<User>,
    categoryList: MutableList<Categories>,
): Map<String, Map<String, Any?>> {
    val errors = mutableMapOf<String, Map<String, Any?>>()

    // Валидация Title
    val title = data["Title"]

    if (title == null || title !is String || title.isEmpty()) {
        errors["Title"] = mutableMapOf("Value" to data["Title"], "Error" to "Ожидается строка")
    }

//        //Валидация RegistationDateTime
    if (data.containsKey("RegistrationDateTime")) {
        try {
            LocalDateTime.parse(data["RegistrationDateTime"].toString())
        } catch (e: DateTimeParseException) {
            errors["RegistrationDateTime"] = mutableMapOf("Value" to data["RegistrationDateTime"], "Error" to "Ожидается дата и время")
        }
    }
//
//        //Валидация StartDateTime
    if (data.containsKey("StartDateTime")) {
        try {
            LocalDateTime.parse(data["StartDateTime"].toString())
        } catch (e: DateTimeParseException) {
            errors["StartDateTime"] = mutableMapOf("Value" to data["StartDateTime"], "Error" to "Ожидается дата и время")
        }
    }
//
    if (data.containsKey("EndDateTime")) {
        try {
            LocalDateTime.parse(data["EndDateTime"].toString())
        } catch (e: DateTimeParseException) {
            errors["EndDateTime"] = mutableMapOf("Value" to data["EndDateTime"], "Error" to "Ожидается дата и время")
        }
    }
//
    if (data.containsKey("Importance")) {
        val validImportanceLevels = listOf("очень низкий", "низкий", "обычный", "высокий", "очень высокий", "критический")
        if (data["Importance"] !in validImportanceLevels) {
            errors["Importance"] = mutableMapOf("Value" to data["Importance"], "Error" to "Ожидается приоритет за списка")
        }
    }
//
    if (data.containsKey("Urgency")) {
        if (data["Urgency"] !is Boolean) {
            errors["Urgency"] = mutableMapOf("Value" to data["Urgency"], "Error" to "Ожидается логическое значение")
        }
    }
//
    if (data.containsKey("Percentage")) {
        val per = data["Percentage"].toString().toIntOrNull()
        if (per != null) {
            if (per < 0 || per > 100) {
                errors["Percentage"] = mutableMapOf("Value" to data["Percentage"], "Error" to "Ожидается натуральное число от 0 до 100")
            }
        } else {
            errors["Percentage"] = mutableMapOf("Value" to data["Percentage"], "Error" to "Ожидается натуральное число от 0 до 100")
        }
    }
//
    if (data.containsKey("Description")) {
        if (data["Description"] !is String) {
            errors["Description"] = mutableMapOf("Value" to data["Description"], "Error" to "Ожидается строка")
        }
    }
//
    val author = data["Author"]?.toString()
    if (author.isNullOrBlank()) {
        errors["Author"] = mapOf<String, Any?>("Value" to data["Author"], "Error" to "Параметр обязательный и не может быть пустым")
    } else {
        if (!isValidUUID(author)) {
            errors["Author"] = mutableMapOf("Value" to data["Author"], "Error" to "Ожидается корректное значение UUID")
        } else {
            val user = userList.firstOrNull({ UUID.fromString(author) == it.id })
            if (user == null) {
                errors["Author"] = mutableMapOf("Value" to data["Author"], "Error" to "Ожидается корректное значение UUID")
            }
        }
    }

    val category = data["Category"]
    if (category != null) {
        if (!isValidUUID(category.toString())) {
            errors["Category"] = mutableMapOf("Value" to category, "Error" to "Ожидается корректное значение UUID")
        } else {
            val categoryEx = categoryList.firstOrNull({ it.id == UUID.fromString(category.toString()) })
            if (categoryEx == null) {
                errors["Category"] = mutableMapOf("Value" to null, "Error" to "Ожидается корректное значение UUID")
            }
        }
    } else {
        errors["Category"] = mutableMapOf("Value" to null, "Error" to "Ожидается корректное значение UUID")
    }

    return errors
}

fun validateMathingAuthorCategory(
    categoryList: MutableList<Categories>,
    author: UUID,
    category: UUID,
): Map<String, String> {
    var categoryOwner: String = ""
    for (item in categoryList) {
        if (item.id == category && item.owner != null) {
            categoryOwner = item.owner.toString()
            break
        }
    }
    val result = mutableMapOf<String, String>()
    if (UUID.fromString(categoryOwner) == author) {
        return result
    } else {
        result["Author"] = author.toString()
        result["CategoryOwnerId"] = categoryOwner
        return result
    }
}

// Утилита для UUID
fun isValidUUID(uuid: String): Boolean =
    try {
        UUID.fromString(uuid)
        true
    } catch (e: IllegalArgumentException) {
        false
    }

fun createTask(
    body: Map<String, Any?>,
    title: String,
    author: UUID,
    category: UUID,
): TaskModel {
    val dateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
    return TaskModel(
        id = UUID.randomUUID(),
        title = title,
        registrationDateTime = LocalDateTime.parse(body["RegistrationDateTime"]?.toString() ?: dateTimeNow),
        startDateTime = LocalDateTime.parse(body["StartDateTime"]?.toString() ?: (body["RegistrationDateTime"]?.toString() ?: dateTimeNow)),
        endDateTime = body["EndDateTime"]?.let { LocalDateTime.parse(it.toString()) },
        importance = body["Importance"] as? String ?: "обычный",
        urgency = body["Urgency"] as? Boolean ?: false,
        percentage = (body["Percentage"] as? Number)?.toInt() ?: 0,
        description = body["Description"] as? String ?: "",
        author = author,
        category = category,
    )
}

fun putTask(
    body: Map<String, Any?>,
    title: String,
    author: UUID,
    category: UUID,
    prevTask: TaskModel,
): TaskModel {
    val dateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
    return TaskModel(
        id = prevTask.id,
        title = title,
        registrationDateTime =
            try {
                LocalDateTime.parse(body["RegistrationDateTime"].toString())
            } catch (e: Exception) {
                prevTask.registrationDateTime
            },
        startDateTime =
            try {
                LocalDateTime.parse(body["StartDateTime"].toString())
            } catch (e: Exception) {
                prevTask.startDateTime
            },
        endDateTime = body["EndDateTime"]?.let { LocalDateTime.parse(it.toString()) },
        importance = body["Importance"] as? String ?: prevTask.importance,
        urgency = body["Urgency"] as? Boolean ?: prevTask.urgency,
        percentage = (body["Percentage"] as? Number)?.toInt() ?: prevTask.percentage,
        description = body["Description"] as? String ?: prevTask.description,
        author = author,
        category = category,
    )
}
