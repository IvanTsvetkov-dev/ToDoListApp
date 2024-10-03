package ru.yarsu

import com.beust.jcommander.*
import java.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import ru.yarsu.taskworkflow.*
import java.io.File

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
        .addCommand("list-eisenhower", listEisenHower)
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
    when (commander.parsedCommand){
        "list" -> println("It's list command!")
        "show" -> println("It's show command!")
        "list-eisenhower" -> println("It's list-eisenhower command!")
        "list-time" -> println("It's list-time command!")
        "statistic" -> println("It's statistic command!")
        else -> {
            print("Dont under this command!")
        }
    }

    val mapper = jacksonObjectMapper()
    val dataTask = mutableListOf<TaskModel>()
    for(item in data.drop(1)){
        dataTask.add(
            TaskModel(
            UUID.fromString(item[0]),
            title = item[1],
            registrationDateTime = item[2],
            startDateTime = item[3],
            endDateTime = item[4] ?: "",
            importance = Importance.LOW,      //ИСПРАВИТЬ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            urgency = item[6].toBoolean(),
            percentage = item[7].toInt(),
            description = item[8]
            )
        )
    }
    val workFlowWithTasks = WorkFlowWithTasks()
    val jsonArray = mapper.writeValue(System.out,workFlowWithTasks.getTaskList(dataTask))
    println(jsonArray)
}
