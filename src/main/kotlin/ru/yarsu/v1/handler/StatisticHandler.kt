package ru.yarsu.v1.handler

import org.http4k.core.*
import org.http4k.lens.contentType
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.parseValuesStatistic
import ru.yarsu.v1.serializers.StatisticSerializer

class StatisticHandler(
    private val tasklist: List<TaskModel>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val byDate: String? = request.uri.queries().findSingle("by-date")

        // helpful objects
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val statisticSerializer = StatisticSerializer()

        // TODO handle page.toInt(), recordsPerPage.toInt()
        try {
            val listStatistic =
                workFlowWithTasks.getStatisticDate(
                    parseValuesStatistic(
                        byDate ?: throw IllegalArgumentException("Отсутствует параметр by-date"),
                    ),
                )
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(statisticSerializer.statisticSerializer(listStatistic, parseValuesStatistic(byDate)))
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(statisticSerializer.serializeError(e.message.toString()))
        }
    }
}
