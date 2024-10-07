package ru.yarsu

import com.beust.jcommander.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import java.io.File
import java.time.LocalDateTime
import java.time.format.*
import kotlin.system.exitProcess

fun main(argv: Array<String>) {
    val showTask = ShowTask()
    val taskList = TaskList()
    val listEisenHower = ListEisenHower()
    val listTime = ListTime()
    val statistic = Statistic()
    val statisticByHowReady = StatisticByHowReady()

    val commander: JCommander = JCommander
        .newBuilder()
        .addCommand("list", taskList)
        .addCommand("show", showTask)
        .addCommand("list-importance", listEisenHower)
        .addCommand("list-time", listTime)
        .addCommand("statistic", statistic)
        .addCommand("statistic-by-how-ready", statisticByHowReady)
        .build()

    val dataForView: Any
    try {
        val data: List<List<String>>
        commander.parse(*argv)
        val csvReader = CsvReader()
        data = when{
            taskList.urlFile != "" -> csvReader.readAll(File(taskList.urlFile))
            showTask.urlFile != "" -> csvReader.readAll(File(showTask.urlFile))
            listEisenHower.urlFile != "" -> csvReader.readAll(File(listEisenHower.urlFile))
            listTime.urlFile != "" -> csvReader.readAll(File(listTime.urlFile))
            statistic.urlFile != "" -> csvReader.readAll(File(statistic.urlFile))
            statisticByHowReady.statisticByHowReadyFile != "" -> {
                if (argv.contains("--tasks-file")){
                    throw IllegalArgumentException("--tasks-file не должен указываться!")
                }
                csvReader.readAll(File(statisticByHowReady.statisticByHowReadyFile))
            }
            else -> {
                throw IllegalArgumentException("Пропущен аргумент")
            }
        }

        val dataTask = mutableListOf<TaskModel>()
        for (item in data.drop(1)) {
            dataTask.add(
                TaskModel(
                    UUID.fromString(item[0]),
                    title = item[1],
                    registrationDateTime = item[2],
                    startDateTime = item[3],
                    endDateTime = item[4],
                    importance = parseImportance(item[5]),
                    urgency = item[6].toBoolean(),
                    percentage = item[7].toInt(),
                    description = item[8]
                )
            )
        }
        val workFlowWithTasks = WorkFlowWithTasks(dataTask)
        dataForView = when (commander.parsedCommand) {
            "list" -> workFlowWithTasks.getSortedTaskList()
            "show" -> {
                workFlowWithTasks.getTaskById(UUID.fromString(showTask.taskID))
            }

            "list-importance" -> {
                workFlowWithTasks.getListEisenHower(listEisenHower.important, listEisenHower.urgent)
            }

            "list-time" -> {
                val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.S"
                val format = DateTimeFormatter.ofPattern(dateFormat)
                if(listTime.time == null){
                    throw NullPointerException("Дата не может быть нулевой")
                }
                workFlowWithTasks.getSotrtedListByManyParametresTask(dataTask, LocalDateTime.parse(listTime.time, format))

            }

            "statistic" -> {
                val valueStatic: String = statistic.valueStatistic
                    ?: throw NullPointerException("ValueStatic нее может быть равным null")
                workFlowWithTasks.getStatisticDate(parseValuesStatistic(valueStatic))
                return
            }

            "statistic-by-how-ready" -> {
                workFlowWithTasks.getStatisticByHowReady()
                return
            }

            else -> {
                println("Не передана ни одна команда!Документация:")
                commander.usage()
                return
            }
        }
    } catch (e: Exception){
        System.err.println("Ошибка! Приложение использовано некорретно. Читайте документацию! Подробности ошибки: $e")
        commander.usage()
        exitProcess(1)
    }

    //Вывод для высокоуровневого интерфейса
    val mapper = jacksonObjectMapper()
    val printer = DefaultPrettyPrinter()
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .writer(printer)
        .writeValue(System.out, dataForView)
}
