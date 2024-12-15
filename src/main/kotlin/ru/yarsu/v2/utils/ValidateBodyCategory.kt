package ru.yarsu.v2.utils

import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.User
import java.util.UUID

fun validateBodyCategory(
    description: String?,
    owner: String?,
): Map<String, Map<String, Any?>> {
    val errors = mutableMapOf<String, MutableMap<String, Any?>>()

    if (description.isNullOrBlank()) {
        errors["Description"] = mutableMapOf("Value" to description, "Error" to "Описание не должно быть пустым")
    }

    if (owner == null) {
        errors["Owner"] = mutableMapOf("Value" to owner, "Error" to "Ожидается значение UUID")
    } else {
        if (owner.isBlank()) {
            errors["Owner"] = mutableMapOf("Value" to owner, "Error" to "Ожидается значение UUID")
        } else if (!isValidUUID(owner)) {
            errors["Owner"] = mutableMapOf("Value" to owner, "Error" to "Неверное значение UUID")
        }
    }

    return errors
}

fun putCategory(
    prevCategory: Categories,
    description: String,
    owner: UUID,
): Categories =
    Categories(
        id = prevCategory.id,
        description = description,
        color = prevCategory.color,
        owner = owner,
    )

fun changeOwnerTaskAfterChangeCategory(
    taskList: MutableList<TaskModel>,
    categoryUUID: UUID,
    authorUUID: UUID,
): MutableList<TaskModel> {
    for (item in taskList) {
        if (item.category == categoryUUID) {
            item.author = authorUUID
        }
    }
    return taskList
}

// собираем задачи, у которых одинаковая категория, но разные владельцы
fun validateOwnersTask(
    taskList: MutableList<TaskModel>,
    categoryUUID: UUID,
    userList: MutableList<User>,
): List<Map<String, String>> {
    // Фильтруем задачи по указанной категории
    val filteredTasks =
        taskList.filter {
            it.category == categoryUUID
        }

    // Создадим хранилище для уникальных задач с разными авторами
    val tasksGroupedByAuthor = mutableMapOf<UUID, MutableList<TaskModel>>()

    // Группируем задачи по автору
    for (task in filteredTasks) {
        tasksGroupedByAuthor.computeIfAbsent(task.author) { mutableListOf() }.add(task)
    }

    // Собираем результаты
    val results = mutableListOf<Map<String, String>>()

    // Проверяем, есть ли несколько задач от разных авторов для одной категории
    for (authorTasks in tasksGroupedByAuthor.values) {
        if (authorTasks.size > 1) {
            for (task in authorTasks) {
                results.add(
                    mapOf<String, String>(
                        "TaskId" to task.id.toString(),
                        "TaskTitle" to task.title,
                        "Author" to task.author.toString(),
                        "AuthorLogin" to (userList.firstOrNull { it.id == task.author }?.login ?: "неизвестный автор"),
                    ),
                )
            }
        }
    }

    return results
}
