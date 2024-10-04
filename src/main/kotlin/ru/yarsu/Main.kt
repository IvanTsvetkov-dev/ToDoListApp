package ru.yarsu

import com.beust.jcommander.*
import com.fasterxml.jackson.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import java.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import ru.yarsu.taskworkflow.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


fun main(argv: Array<String>) {
    val args = Args()
    val showTask = ShowTask()
    val taskList = TaskList()
    val listEisenHower = ListEisenHower()
    val listTime = ListTime()
    val statistic = Statistic()

    val commander: JCommander = JCommander
        .newBuilder()
        .addObject(args)
        .addCommand("list", taskList)
        .addCommand("show", showTask)
        .addCommand("list-importance", listEisenHower)
        .addCommand("list-time", listTime)
        .addCommand("statistic", statistic)
        .build()

    var data: List<List<String>> = listOf()
    try{
        commander.parse(*argv)
        val csvReader = CsvReader()
        data = csvReader.readAll(File(args.urlFile!!))
    } catch (e: ParameterException){
        System.err.println("Don't have required arguments")
        return
    }
    val dataTask = mutableListOf<TaskModel>()
    for(item in data.drop(1)){
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
    val workFlowWithTasks = WorkFlowWithTasks()
    val information: Any = when (commander.parsedCommand){
        "list" -> workFlowWithTasks.getSortedTaskList(dataTask)
        "show" -> {
            try {
                workFlowWithTasks.getTaskById(dataTask, UUID.fromString(showTask.taskID))
            } catch (e: NullPointerException){
                System.err.println("Запись с таким ID не найдена")
            } catch (e: IllegalArgumentException){
                System.err.println("Указан некорректный ID")
            }
        }
        "list-importance" -> {
            workFlowWithTasks.getListEisenHower(dataTask, listEisenHower.important, listEisenHower.urgent)
        }
        "list-time" -> {
            val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.S"
            val format = DateTimeFormatter.ofPattern(dateFormat)
            try{
                workFlowWithTasks.getSotrtedListByManyParametresTask(dataTask, LocalDateTime.parse(listTime.time!!, format))
            }catch (e: DateTimeParseException){
                System.err.println("Указан некорректная дата. Шаблон: $dateFormat")
            }
        }
        "statistic" -> println("It's statistic command!")
        else -> {
            print("Don't under this command!")
        }
    }
    val mapper = jacksonObjectMapper()
    mapper.enable(SerializationFeature.INDENT_OUTPUT).setSerializationInclusion(JsonInclude.Include.NON_NULL).writerWithDefaultPrettyPrinter().writeValue(System.out, information)
}
