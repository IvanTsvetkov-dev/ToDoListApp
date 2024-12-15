package ru.yarsu.v2.utils

import ru.yarsu.Categories
import ru.yarsu.TaskModel
import java.util.UUID

fun createErrorLog(
    taskList: List<TaskModel>,
    categoriesList: List<Categories>,
    userUUID: UUID,
): MutableMap<String, MutableList<MutableMap<String, String>>> {
    val errors = mutableMapOf<String, MutableList<MutableMap<String, String>>>()

    val tasks = mutableListOf<MutableMap<String, String>>()

    val sortedListTasks = taskList.sortedWith(compareBy<TaskModel> { it.id })
    for (task in sortedListTasks) {
        if (task.author == userUUID) {
            tasks.add(mutableMapOf("Id" to task.id.toString(), "Title" to task.title))
        }
    }

    // Сбор категорий
    val categories = mutableListOf<MutableMap<String, String>>()

    val sortedList = categoriesList.sortedWith(compareBy<Categories> { it.id })

    for (category in sortedList) {
        if (category.owner == userUUID) {
            categories.add(mutableMapOf("Id" to category.id.toString(), "Description" to category.description))
        }
    }

    // Добавление собранных данных в итоговую карту ошибок
    errors["Tasks"] = tasks
    errors["Categories"] = categories

    return errors
}
