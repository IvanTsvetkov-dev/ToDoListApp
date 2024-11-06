package ru.yarsu.v1.handler

import org.http4k.core.*
import org.http4k.lens.contentType
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.pagination
import ru.yarsu.v1.serializers.EisenHowerListSerializer
import ru.yarsu.v1.serializers.ListTimeSerializer
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

        try{
            if(page.toIntOrNull() == null){throw IllegalArgumentException("Некорректное значение параметра page. Ожидается натуральное число, но получено $page")}
            if(recordsPerPage.toIntOrNull() == null) {throw IllegalArgumentException("Некорректное значение параметра records-per-page. Ожидается 5 10 20 50, но получено $recordsPerPage")}
            val listTime = pagination(workFlowWithTasks.getListTime(tasklist, LocalDateTime.parse(time ?: throw IllegalArgumentException("Некорректное значение параметры time. Ожидается дата и время в формате ISO, но получена пустая строка"), DateTimeFormatter.ISO_DATE_TIME)), page.toInt(), recordsPerPage.toInt())
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(listTimeSerializer.listTimeSerialize(listTime))
        }catch (e: IllegalArgumentException){
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(listTimeSerializer.serializeError(e.message.toString()))
        }catch (e: DateTimeParseException){
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(listTimeSerializer.serializeError("Неправильный формат параметра time. Ожидался формат в ISO, а получен $time"))
        }

    }
}
