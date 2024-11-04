package ru.yarsu.handler.v1

import org.http4k.core.*
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.serializers.EisenHowerListSerializer

class EisenhowerListHandler(private val tasklist: List<TaskModel>) : HttpHandler {
    override fun invoke(request: Request): Response {
        //get query parameters
        val important: String? = request.uri.queries().findSingle("important")
        val urgent: String? = request.uri.queries().findSingle("urgent")
        val page: String = request.uri.queries().findSingle("page") ?: "1"
        val recordsPerPage: String = request.uri.queries().findSingle("records-per-page") ?: "10"

        //helpful objects
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val eisenHowerListSerializer = EisenHowerListSerializer()

        //TODO handle page.toInt(), recordsPerPage.toInt()
        try{
            if(important.toString() !in listOf("null", "true", "false")){throw IllegalArgumentException("Неверно переданный аргумент важности. Ожидалось true, false, null(в случае отсутствия), а не $important")}
            if(urgent.toString() !in listOf("null", "true", "false")){throw IllegalArgumentException("Неверно переданный аргумент срочности. Ожидалось true, false, null(в случае отсутствия), а не $urgent")}
            val listEisenHower = workFlowWithTasks.getListEisenHower(if (important == null) null else important.toBoolean(), if (urgent == null) null else urgent.toBoolean(), page.toInt(), recordsPerPage.toInt())
            return Response(Status.OK).body(eisenHowerListSerializer.eisenHowerList(listEisenHower))
        }catch (e: IllegalArgumentException){
            return Response(Status.BAD_REQUEST).body(eisenHowerListSerializer.serializeError(e.message.toString()))
        }


    }
}

