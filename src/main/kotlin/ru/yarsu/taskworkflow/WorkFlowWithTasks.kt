package ru.yarsu.taskworkflow

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import ru.yarsu.ValuesStatistic
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.mutableListOf as mutableListOf

class WorkFlowWithTasks(
    var tasksData: List<TaskModel>
) {
    fun getSortedTaskList() : TaskCommandList
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

        val viewTotalSortedFilteredTaskList: TaskCommandList = TaskCommandList(
            totalSortedFilteredTaskList
        )

        return viewTotalSortedFilteredTaskList
    }
    fun getTaskById(id: UUID) : Task
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
    fun getListEisenHower(important: Boolean?, urgent: Boolean?) : ListImportance {
        val filteredTasks = tasksData.filter { task ->
            (important == null ||
                    (important && task.importance in listOf(Importance.HIGH, Importance.VERYHIGH, Importance.CRITICAL)) ||
                    (important == false && task.importance in listOf(
                        Importance.VERYLOW,
                        Importance.LOW,
                        Importance.DEFAULT
                    ))) &&
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
    fun getSotrtedListByManyParametresTask(inputDateTime: LocalDateTime) : TaskForListTime {
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

    fun getStatisticDate(typeStatistic: ValuesStatistic) : Unit {
        val dayCount: MutableMap<String, Int> = mutableMapOf()
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")

        val dayOfWeekTranslations = mapOf(
            "MONDAY" to "Понедельник",
            "TUESDAY" to "Вторник",
            "WEDNESDAY" to "Среда",
            "THURSDAY" to "Четверг",
            "FRIDAY" to "Пятница",
            "SATURDAY" to "Суббота",
            "SUNDAY" to "Воскресенье"
        )

        val weekDaysOrder = listOf(
            "Понедельник", "Вторник", "Среда", "Четверг",
            "Пятница", "Суббота", "Воскресенье", "Не заполнено"
        )

        for (task in tasksData) {
            val dateString = when (typeStatistic) {
                ValuesStatistic.REGISTRATION -> task.registrationDateTime
                ValuesStatistic.START -> task.startDateTime
                ValuesStatistic.END -> task.endDateTime ?: ""
            }

            if (dateString.isNotEmpty()) {
                val date = LocalDateTime.parse(dateString, format)
                val dayOfWeek = dayOfWeekTranslations[date.dayOfWeek.name] ?: date.dayOfWeek.name
                dayCount[dayOfWeek] = dayCount.getOrDefault(dayOfWeek, 0) + 1
            } else if (typeStatistic == ValuesStatistic.END) {
                dayCount["Не заполнено"] = dayCount.getOrDefault("Не заполнено", 0) + 1
            }
        }

        val factory: JsonFactory = JsonFactory()
        val outputGenerator: JsonGenerator = factory.createGenerator(System.out)
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        outputGenerator.prettyPrinter = printer

        with(outputGenerator) {
            writeStartObject()

            writeFieldName(when(typeStatistic){
                ValuesStatistic.REGISTRATION -> "statisticByRegistrationDateTime"
                ValuesStatistic.START -> "statisticByStartDateTime"
                ValuesStatistic.END -> "statisticByEndDateTime"
            })

            writeStartArray()

            for (day in weekDaysOrder) {
                dayCount[day]?.let { count ->
                    writeStartObject()
                    writeFieldName(day)
                    writeNumber(count)
                    writeEndObject()
                }
            }
            writeEndArray()

            writeEndObject()
            close()
        }
    }

}
