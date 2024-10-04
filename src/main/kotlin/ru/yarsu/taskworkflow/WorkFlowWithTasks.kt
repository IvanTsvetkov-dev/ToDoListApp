package ru.yarsu.taskworkflow

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class WorkFlowWithTasks {
    fun getSortedTaskList(tasks: List<TaskModel>) : TaskList
    {
        val sortedFilteredTasks = tasks.sortedBy {LocalDateTime.parse(it.registrationDateTime)}

        val totalSortedFilteredTaskList = mutableListOf<Tasks>()

        sortedFilteredTasks.forEach({task ->
            totalSortedFilteredTaskList.add(
                Tasks(
                    id = task.id,
                    title = task.title,
                    isClosed = task.percentage == 100
                )
            )
        })

        val viewTotalSortedFilteredTaskList: TaskList = TaskList(
            totalSortedFilteredTaskList
        )

        return viewTotalSortedFilteredTaskList
    }
    fun getTaskById(tasksData: List<TaskModel>, id: UUID) : Task
    {
        var taskById = tasksData.find { it.id == id }
        if (taskById == null){
            throw NullPointerException()
        }
        return Task(
            id = id,
            task = taskById
        )
    }
}
