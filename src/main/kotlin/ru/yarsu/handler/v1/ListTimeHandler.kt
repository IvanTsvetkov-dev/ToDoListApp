package ru.yarsu.handler.v1

import org.http4k.core.*
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.pagination
import ru.yarsu.serializers.EisenHowerListSerializer
import ru.yarsu.serializers.ListTimeSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ListTimeHandler(private val tasklist: List<TaskModel>) : HttpHandler {
    override fun invoke(request: Request): Response {
        val time: String? = request.uri.queries().findSingle("time")
        val page: String = request.uri.queries().findSingle("page") ?: "1"
        val recordsPerPage: String = request.uri.queries().findSingle("records-per-page") ?: "10"

        //helpful objects
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val listTimeSerializer = ListTimeSerializer()

        //TODO handle page.toInt(), recordsPerPage.toInt()
        try{
            val listTime = pagination(workFlowWithTasks.getListTime(tasklist, LocalDateTime.parse(time ?: throw IllegalArgumentException("Параметр time обязательный"), DateTimeFormatter.ISO_DATE_TIME)), page.toInt(), recordsPerPage.toInt())
            return Response(Status.OK).body(listTimeSerializer.listTimeSerialize(listTime))
        }catch (e: IllegalArgumentException){
            return Response(Status.BAD_REQUEST).body(listTimeSerializer.serializeError(e.message.toString()))
        }catch (e: DateTimeParseException){
            return Response(Status.BAD_REQUEST).body(listTimeSerializer.serializeError("Неправильный формат параметра time. Ожидался формат в ISO, а получен $time"))
        }

    }
}
