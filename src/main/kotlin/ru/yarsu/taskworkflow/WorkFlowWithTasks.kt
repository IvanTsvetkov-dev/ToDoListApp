package ru.yarsu.taskworkflow

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.mutableListOf as mutableListOf

class WorkFlowWithTasks {
    fun getSortedTaskList(tasksData: List<TaskModel>) : TaskList
    {
        val sortedFilteredTasks = tasksData.sortedBy {LocalDateTime.parse(it.registrationDateTime)}

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
    fun getListEisenHower(tasksData: List<TaskModel>, important: Boolean?, urgent: Boolean?) : ListImportance{
        val filteredTasks = tasksData.filter { task ->
            (important == null ||
                    (important && task.importance in listOf(Importance.HIGH, Importance.VERYHIGH, Importance.CRITICAL)) ||
                    (important == false && task.importance in listOf(Importance.VERYLOW, Importance.LOW, Importance.DEFAULT))) &&
                    (urgent == null || task.urgency == urgent)
        }
        val taskForListImportance = mutableListOf<TaskForListImportance>()
        filteredTasks.forEach({task ->
            taskForListImportance.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance,
                    urgency = task.urgency,
                    percentage = task.percentage
                )
            )
        })

        return ListImportance(
            important = important,
            urgent = urgent,
            tasks = taskForListImportance
        )
    }
    fun getSotrtedListByManyParametresTask(tasksData: List<TaskModel>, inputDateTime: LocalDateTime) : TaskForListTime{
        var format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")
        val listSorted = tasksData.filter { task ->
            val taskStartDateTime = LocalDateTime.parse(task.startDateTime, format)
            val taskregistrationDateTime = LocalDateTime.parse(task.registrationDateTime, format)
            taskStartDateTime.isBefore(inputDateTime) && taskregistrationDateTime.isBefore(inputDateTime) && task.percentage < 100
        }.sortedWith(                                              //sortedWith принимает собственный Comparator
            compareByDescending<TaskModel> { it.importance.order } //убывание важности
                .thenByDescending { it.urgency }
                .thenBy { LocalDateTime.parse(it.registrationDateTime, format) }
                .thenBy { it.id }
        )

        val taskList = mutableListOf<TaskForListImportance>()
        listSorted.forEach({task ->
            taskList.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance,
                    urgency = task.urgency,
                    percentage = task.percentage
                )
            )
        })

        return TaskForListTime(
            time = inputDateTime.toString(),
            tasks = taskList
        )

    }

}
