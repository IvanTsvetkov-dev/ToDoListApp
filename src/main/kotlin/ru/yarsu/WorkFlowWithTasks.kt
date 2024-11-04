package ru.yarsu

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.mutableListOf as mutableListOf
import java.util.UUID

class WorkFlowWithTasks(
    private val tasksData: List<TaskModel>
) {
    fun getSortedTaskList() : List<TasksForListCommand>
    {
        val sortedFilteredTasks = tasksData.sortedWith(
            compareBy<TaskModel>{ it.registrationDateTime }.thenBy { it.id }
        )

        val totalSortedFilteredTaskList = mutableListOf<TasksForListCommand>()

        sortedFilteredTasks.forEach(
            {
            task ->
            totalSortedFilteredTaskList.add(
                TasksForListCommand(
                    id = task.id,
                    title = task.title,
                    isClosed = task.percentage == 100
                )
            )
        })
        return totalSortedFilteredTaskList
    }
    fun getTaskById(id: UUID) : TaskModel
    {
        val taskById = tasksData.find { it.id == id }
        if (taskById == null){
            throw NullPointerException("Задача не найдена")
        }
        return taskById
    }
    fun getListEisenHower(important: Boolean?, urgent: Boolean?) : List<TaskForListImportance>{
        if(important == null && urgent == null){
            throw IllegalArgumentException("Некорретные параметры важности и срочности.Хотя бы один из них должен быть указан")
        }
        val importantStatusTask = listOf(Importance.CRITICAL.importance, Importance.VERYHIGH.importance, Importance.HIGH.importance)
        val unimportantStatusTask = listOf(Importance.LOW.importance, Importance.VERYLOW.importance, Importance.DEFAULT.importance)
        val filteredTasks = tasksData.filter {
            task -> (important == null || (important && task.importance.importance in importantStatusTask) || (!important && task.importance.importance in unimportantStatusTask))
                &&
                (urgent == null || ((urgent && task.urgency == urgent) || (!urgent && task.urgency == urgent)))
        }.sortedWith(compareBy<TaskModel> { it.registrationDateTime }
            .thenBy { it.id })

        val taskForListImportance = mutableListOf<TaskForListImportance>()
        filteredTasks.forEach({task ->
            taskForListImportance.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance.importance,
                    urgency = task.urgency,
                    percentage = task.percentage
                )
            )
        })
        return taskForListImportance
    }
    fun getListTime(tasksData: List<TaskModel>, inputDateTime: LocalDateTime?) : List<TaskForListImportance> {
        val listSorted = tasksData.filter { task -> (task.startDateTime < inputDateTime) && (task.percentage < 100)
        }.sortedWith(compareByDescending<TaskModel> {it.importance.ordinal}
            .thenByDescending{it.urgency}
            .thenBy { it.registrationDateTime }
            .thenBy {it.id})

        val taskList = mutableListOf<TaskForListImportance>()
        listSorted.forEach({task ->
            taskList.add(
                TaskForListImportance(
                    id = task.id,
                    title = task.title,
                    importance = task.importance.importance,
                    urgency = task.urgency,
                    percentage = task.percentage
                )
            )
        })
        return taskList

    }
//
//
    fun getStatisticDate(typeStatistic: ValuesStatistic) : Unit {
        val dayCount: MutableMap<String, Int> = mutableMapOf()

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

            if (dateString.toString().isNotEmpty()) {
                val date = LocalDateTime.parse(dateString.toString(), DateTimeFormatter.ISO_DATE_TIME)
                val dayOfWeek = dayOfWeekTranslations[date.dayOfWeek.name] ?: date.dayOfWeek.name
                dayCount[dayOfWeek] = dayCount.getOrDefault(dayOfWeek, 0) + 1
            } else if (typeStatistic == ValuesStatistic.END) {
                dayCount["Не заполнено"] = dayCount.getOrDefault("Не заполнено", 0) + 1
            }
        }

//        val outputGenerator = createOutputGenerator()
//
//        with(outputGenerator) {
//            writeStartObject()
//
//            writeFieldName(when(typeStatistic){
//                ValuesStatistic.REGISTRATION -> "statisticByRegistrationDateTime"
//                ValuesStatistic.START -> "statisticByStartDateTime"
//                ValuesStatistic.END -> "statisticByEndDateTime"
//            })
//
//            writeStartArray()
//
//            for (day in weekDaysOrder) {
//                dayCount[day]?.let { count ->
//                    writeStartObject()
//                    writeFieldName(day)
//                    writeNumber(count)
//                    writeEndObject()
//                }
//            }
//            writeEndArray()
//
//            writeEndObject()
//            close()
//        }
    }
//    fun getStatisticByHowReady() : Unit{
//        val statisticCount: MutableMap<String, Int> = mutableMapOf()
//        val statisticStatus = listOf(
//            "готова", "почти готова", "в процессе", "немного начата",
//            "не начата"
//        )
//        for(task in tasksData){
//            when(task.percentage){
//                100 -> {
//                    if(!statisticCount.containsKey("готова")){
//                        statisticCount["готова"] = 1
//                    }else{
//                        val value = statisticCount.getValue("готова") + 1
//                        statisticCount["готова"] = value
//                    }
//                }
//                in 85..99 ->{
//                    if(!statisticCount.containsKey("почти готова")){
//                        statisticCount["почти готова"] = 1
//                    }else{
//                        val value = statisticCount.getValue("почти готова") + 1
//                        statisticCount["почти готова"] = value
//                    }
//                }
//                in 16..84 ->{
//                    if(!statisticCount.containsKey("в процессе")){
//                        statisticCount["в процессе"] = 1
//                    } else{
//                        val value = statisticCount.getValue("в процессе") + 1
//                        statisticCount["в процессе"] = value
//                    }
//                }
//                in 1..14 -> {
//                    if(!statisticCount.containsKey("немного начата")){
//                        statisticCount["немного начата"] = 1
//                    } else{
//                    val value = statisticCount.getValue("немного начата") + 1
//                        statisticCount["немного начата"] = value
//                    }
//                }
//                else -> {
//                    if(!statisticCount.containsKey("не начата")){
//                        statisticCount["не начата"] = 1
//                    } else{
//                        val value = statisticCount.getValue("не начата") + 1
//                        statisticCount["не начата"] = value
//                    }
//                }
//
//            }
//        }
//        val outputGenerator = createOutputGenerator()
//
//        with(outputGenerator) {
//            writeStartObject()
//
//            writeFieldName("statisticByHowReady")
//
//            writeStartObject()
//            for (statistic in statisticStatus) {
//                statisticCount[statistic]?.let { count ->
//                    writeFieldName(statistic)
//                    writeNumber(count)
//                }
//            }
//            writeEndObject()
//
//            writeEndObject()
//            close()
//        }
//    }

}
