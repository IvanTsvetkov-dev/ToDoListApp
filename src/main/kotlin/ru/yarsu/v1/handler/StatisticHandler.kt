package ru.yarsu.v1.handler

import org.http4k.core.*
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.pagination
import ru.yarsu.parseValuesStatistic
import ru.yarsu.v1.serializers.ListTimeSerializer
import ru.yarsu.v1.serializers.StatisticSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class StatisticHandler(private val tasklist: List<TaskModel>) : HttpHandler {
    override fun invoke(request: Request): Response {
        val byDate: String? = request.uri.queries().findSingle("by-date")

        //helpful objects
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val statisticSerializer = StatisticSerializer()

        //TODO handle page.toInt(), recordsPerPage.toInt()
        try{
            val listStatistic = workFlowWithTasks.getStatisticDate(parseValuesStatistic(byDate ?: throw IllegalArgumentException("Некорректное значение типа статистики. Для параметра by-date ожидается значение типа статистики, но получено пустое значение")))
            return Response(Status.OK).body(statisticSerializer.statisticSerializer(listStatistic, parseValuesStatistic(byDate)))
        }catch (e: IllegalArgumentException){
            return Response(Status.BAD_REQUEST).body(statisticSerializer.serializeError(e.message.toString()))
        }
    }
}
