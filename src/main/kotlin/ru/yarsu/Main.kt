package ru.yarsu

import com.beust.jcommander.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import org.http4k.core.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.*
import kotlin.system.exitProcess

import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.web.routes.BaseUrlHandler
import ru.yarsu.web.routes.TaskListHandler
import ru.yarsu.web.routes.TaskShowHandler


fun main(argv: Array<String>) {
    val args = Args()
//
    val commander: JCommander = JCommander
        .newBuilder()
        .addObject(args)
        .build()
    try {
        val data: List<List<String>>
        commander.parse(*argv)
        val pathToTasksFile = args.urlFile ?: throw ParameterException("Error: missing option --tasks-file")
        val pathToUsersFile = args.userFile ?: throw ParameterException("Error: missing option --users-file")

//        val workFlowWithTasks = WorkFlowWithTasks(readTaskFileCsv(pathToTasksFile))
        val taskList: List<TaskModel> = readTaskFileCsv(pathToTasksFile)
        //routes является http обработчиком типа RoutingHttpHandler.
        //
        val app = routes( //bind возвращает PathMethod,связывая "uri" с http методом,
            "/" bind Method.GET to {request: Request ->  Response(Status.OK).body("Its ToDoListApp")},
            "/ping" bind Method.GET to BaseUrlHandler(), //связывает строку с методом по его обработке
            "/v1" bind routes(
                "/list-tasks" bind Method.GET to TaskListHandler(),
                "/task/{task-id}" bind Method.GET to TaskShowHandler(taskList)
            )
        )
        val server = app.asServer(Netty(args.numberPort ?: throw ParameterException("Error: missing option --port"))).start()
    } catch (e: Exception){
        System.err.println("Ошибка! Приложение использовано некорретно. Читайте документацию! Подробности ошибки: $e")
//        commander.usage()
        exitProcess(1)
    }
}
fun readTaskFileCsv(pathToTasksFile : String) : List<TaskModel>{
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToTasksFile))

    val dataTask = mutableListOf<TaskModel>()
    for (item in data.drop(1)) {
        dataTask.add(
            TaskModel(
                UUID.fromString(item[0]),
                title = item[1],
                registrationDateTime = LocalDateTime.parse(item[2], DateTimeFormatter.ISO_DATE_TIME),
                startDateTime = LocalDateTime.parse(item[3]),
                endDateTime = if (item[4] == "") null else LocalDateTime.parse(item[4]),
                importance = parseImportance(item[5]),
                urgency = item[6].toBoolean(),
                percentage = item[7].toInt(),
                description = item[8]
            )
        )
    }
    return dataTask
}

//    val mapper = jacksonObjectMapper()
//    val printer = DefaultPrettyPrinter()
//    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
//    mapper.enable(SerializationFeature.INDENT_OUTPUT)
//        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//        .writer(printer)
//        .writeValue(System.out, dataForView)
