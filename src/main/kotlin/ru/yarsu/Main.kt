package ru.yarsu

import com.beust.jcommander.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import ru.yarsu.taskworkflow.*
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.*

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

    val data: List<List<String>>
    try{
        commander.parse(*argv)
        val csvReader = CsvReader()
        data = csvReader.readAll(File(args.urlFile))
    } catch (e: ParameterException){
        System.err.println("Не указан обязательный аргумент!")
        return
    } catch (e: FileNotFoundException){
        System.err.println("Файл не существует!")
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
    val workFlowWithTasks = WorkFlowWithTasks(dataTask)
    val information: Any = when (commander.parsedCommand){
        "list" -> workFlowWithTasks.getSortedTaskList()
        "show" -> {
            try {
                workFlowWithTasks.getTaskById(UUID.fromString(showTask.taskID))
            } catch (e: NullPointerException){
                System.err.println("Запись с таким ID не найдена")
            } catch (e: IllegalArgumentException){
                System.err.println("Указан некорректный ID")
            }
        }
        "list-importance" -> {
            workFlowWithTasks.getListEisenHower(listEisenHower.important, listEisenHower.urgent)
        }
        "list-time" -> {
            val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.S"
            val format = DateTimeFormatter.ofPattern(dateFormat)
            try{
                workFlowWithTasks.getSotrtedListByManyParametresTask(LocalDateTime.parse(listTime.time!!, format))
            }catch (e: DateTimeParseException){
                System.err.println("Указан некорректная дата. Шаблон: $dateFormat")
            }
        }
        "statistic" -> {
            workFlowWithTasks.getStatisticDate(parseValuesStatistic(statistic.valueStatistic!!))
            return
        }
        else -> {
            print("Don't under this command!")
        }
    }
    //Вывод для высокоуровневого интерфейса
    val mapper = jacksonObjectMapper()
    val printer = DefaultPrettyPrinter()
    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .writer(printer)
        .writeValue(System.out, information)
}
