package ru.yarsu.v1.handler

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
import ru.yarsu.pagination
import ru.yarsu.v1.serializers.EisenHowerListSerializer

class EisenhowerListHandler(
    private val tasklist: List<TaskModel>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        // get query parameters
        val important: String? = request.uri.queries().findSingle("important")
        val urgent: String? = request.uri.queries().findSingle("urgent")
        val page: String = request.uri.queries().findSingle("page") ?: "1"
        val recordsPerPage: String = request.uri.queries().findSingle("records-per-page") ?: "10"

        // helpful objects
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val eisenHowerListSerializer = EisenHowerListSerializer()

        try {
            if (important.toString() !in
                listOf("null", "true", "false")
            ) {
                throw IllegalArgumentException(
                    "Некорректная важность задачи. Для параметра important ожидается логическое значение, но получено пустое значение",
                )
            }
            if (urgent.toString() !in
                listOf("null", "true", "false")
            ) {
                throw IllegalArgumentException(
                    "Некорректная срочность задачи. Для параметра urgent ожидается логическое значение, но получено $urgent",
                )
            }
            if (page.toIntOrNull() ==
                null
            ) {
                throw IllegalArgumentException("Некорректное значение параметра page. Ожидается натуральное число, но получено $page")
            }
            if (recordsPerPage.toIntOrNull() ==
                null
            ) {
                throw IllegalArgumentException(
                    "Некорректное значение параметра records-per-page. Ожидается 5 10 20 50, но получено $recordsPerPage",
                )
            }
            val listEisenHower =
                pagination(
                    workFlowWithTasks.getListEisenHower(
                        if (important ==
                            null
                        ) {
                            null
                        } else {
                            important.toBoolean()
                        },
                        if (urgent ==
                            null
                        ) {
                            null
                        } else {
                            urgent.toBoolean()
                        },
                    ),
                    page.toInt(),
                    recordsPerPage.toInt(),
                )
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(eisenHowerListSerializer.eisenHowerList(listEisenHower))
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(eisenHowerListSerializer.serializeError(e.message.toString()))
        }
    }
}
