package ru.yarsu

import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import java.util.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.doyaaaaaken.kotlincsv.client.CsvReader
import ru.yarsu.jcommander.Args
import ru.yarsu.taskworkflow.Importance
import ru.yarsu.taskworkflow.TaskModel
import ru.yarsu.taskworkflow.WorkFlowWithTasks
import java.io.File

fun main(argv: Array<String>) {
    val args = Args()
    val commander: JCommander = JCommander
        .newBuilder()
        .addObject(args)
        .build()
    var data: List<List<String>> = listOf()
    try{
        commander.parse(*argv)
        val csvReader = CsvReader()
        data = csvReader.readAll(File(args.urlFile!!))
    }catch (e: NullPointerException){
        println("")
        return
    } catch (e: ParameterException){
        System.err.println("Don't have required arguments")
        return
    }



    val mapper = jacksonObjectMapper()
    val dataTask = mutableListOf<TaskModel>()
    for(item in data){
        if(item[0] == "Id"){
            continue
        }
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
//






    //format(DateTimeFormatter("MMM dd yyyy, hh:mm:ss a"))

}
