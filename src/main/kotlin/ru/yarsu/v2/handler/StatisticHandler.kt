package ru.yarsu.v2.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.findSingle
import org.http4k.core.queries
import org.http4k.lens.contentType
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.parseValuesStatistic
import ru.yarsu.v2.serializers.StatisticSerializer

class StatisticHandler(
    private var tasklist: List<TaskModel>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val byDate: String? = request.uri.queries().findSingle("by-date")

        // helpful objects
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val statisticSerializer = StatisticSerializer()

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
