package ru.yarsu.taskworkflow

import java.time.LocalDate
import java.time.LocalDateTime

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
}
