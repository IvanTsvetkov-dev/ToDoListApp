package ru.yarsu

import com.beust.jcommander.*
import java.util.*
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import org.http4k.core.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.*
import kotlin.system.exitProcess

import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.web.routes.applicationRoutes


fun main(argv: Array<String>) {
    val args = Args()
    val commander: JCommander = JCommander
        .newBuilder()
        .addObject(args)
        .build()
    try {
        val data: List<List<String>>
        commander.parse(*argv)

        val pathToTasksFile = args.urlFile ?: throw ParameterException("Error: missing option --tasks-file")

        val pathToUsersFile = args.userFile ?: throw ParameterException("Error: missing option --users-file")

        //Get Router
        val app = applicationRoutes(readTaskFileCsv(pathToTasksFile), readUserFileCsv(pathToUsersFile))

        val server = app.asServer(Netty(args.numberPort ?: throw ParameterException("Error: missing option --port"))).start()
    } catch (e: Exception){
        System.err.println("Ошибка! Приложение использовано некорретно. Читайте документацию! Подробности ошибки: $e")
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
                description = item[8],
                UUID.fromString(item[9])
            )
        )
    }
    return dataTask
}
fun readUserFileCsv(pathToTasksFile: String) : List<User>{
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToTasksFile))

    val dataOfUsers = mutableListOf<User>()

    for(item in data.drop(1)){
        dataOfUsers.add(
            User(
                UUID.fromString(item[0]),
                item[1],
                LocalDateTime.parse(item[2], DateTimeFormatter.ISO_DATE_TIME).toString(),
                item[3]

            )
        )
    }
    return dataOfUsers
}
//    val mapper = jacksonObjectMapper()
//    val printer = DefaultPrettyPrinter()
//    printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
//    mapper.enable(SerializationFeature.INDENT_OUTPUT)
//        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//        .writer(printer)
//        .writeValue(System.out, dataForView)
