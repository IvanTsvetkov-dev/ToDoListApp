package ru.yarsu

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.v2.applicationRoutes
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.system.exitProcess

fun main(argv: Array<String>) {
    val args = Args()
    val commander: JCommander =
        JCommander
            .newBuilder()
            .addObject(args)
            .build()
    try {
        val data: List<List<String>>

        commander.parse(*argv)

        val pathToTasksFile = args.urlFile ?: throw ParameterException("Error: missing option --tasks-file")

        val pathToUsersFile = args.userFile ?: throw ParameterException("Error: missing option --users-file")

        val pathToCategoriesFile = args.categoriesFile ?: throw ParameterException("Error: missing opton --categories-file")

        var taskFile = readTaskFileCsv(pathToTasksFile)

        val app = applicationRoutes(taskFile, readUserFileCsv(pathToUsersFile), readCategoriesFileCsv(pathToCategoriesFile))

//        Runtime.getRuntime().addShutdownHook(object : Thread() {
//            override fun run() {
//                super.run()
//                saveTaskToCsv(taskFile, pathToTasksFile)
//            }
//        })

        app.asServer(Netty(args.numberPort ?: throw ParameterException("Error: missing option --port"))).start()
    } catch (e: Exception) {
        System.err.println("$e")
        exitProcess(1)
    }
}

fun readTaskFileCsv(pathToTasksFile: String): MutableList<TaskModel> {
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
                importance = item[5],
                urgency = item[6].toBoolean(),
                percentage = item[7].toInt(),
                description = item[8],
                UUID.fromString(item[9]),
                UUID.fromString(item[10]),
            ),
        )
    }
    return dataTask
}

fun readUserFileCsv(pathToUserFile: String): MutableList<User> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToUserFile))

    val dataOfUsers = mutableListOf<User>()

    for (item in data.drop(1)) {
        dataOfUsers.add(
            User(
                UUID.fromString(item[0]),
                item[1],
                LocalDateTime.parse(item[2], DateTimeFormatter.ISO_DATE_TIME).toString(),
                item[3],
            ),
        )
    }
    return dataOfUsers
}

fun readCategoriesFileCsv(pathToCategoriesFile: String): MutableList<Categories> {
    val csvReader = CsvReader()
    val data = csvReader.readAll(File(pathToCategoriesFile))

    val dataOfCategories = mutableListOf<Categories>()

    for (item in data.drop(1)) {
        dataOfCategories.add(
            Categories(
                UUID.fromString(item[0]),
                item[1],
                item[2],
                try {
                    UUID.fromString(item[3])
                } catch (e: IllegalArgumentException) {
                    null
                },
            ),
        )
    }
    return dataOfCategories
}

// fun saveTaskToCsv(
//    tasks: Collection<TaskModel>,
//    filePath: String
// ) {
//    val file = File(filePath)
//    val existingIds = mutableSetOf<String>()
//
//
//    if (file.exists() && file.length() > 0) {
//        file.readLines().forEach { line ->
//            val columns = line.split(",")
//            if (columns.isNotEmpty()) {
//                existingIds.add(columns[0])
//            }
//        }
//    }
//
//    csvWriter().open(file, append = true) {
//
//        if (file.length() > 0) {
//            writeRow("")
//        }
//
//
//        if (file.length() == 0L) {
//            writeRow(
//                "Id",
//                "Title",
//                "RegistrationDateTime",
//                "StartDateTime",
//                "EndDateTime",
//                "Importance",
//                "Urgency",
//                "Percentage",
//                "Description",
//                "Author",
//                "Category"
//            )
//        }
//
//
//        for (task in tasks) {
//            if (!existingIds.contains(task.id.toString())) {
//                writeRow(
//                    task.id,
//                    task.title,
//                    task.registrationDateTime,
//                    task.startDateTime,
//                    task.endDateTime,
//                    task.importance,
//                    task.urgency,
//                    task.percentage,
//                    task.description,
//                    task.author,
//                    task.category
//                )
//            }
//        }
//    }

// }
