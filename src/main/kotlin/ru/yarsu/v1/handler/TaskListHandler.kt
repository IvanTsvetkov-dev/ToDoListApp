package ru.yarsu.v1.handler

import org.http4k.core.*
import org.http4k.lens.contentType
import ru.yarsu.TaskModel
import ru.yarsu.TasksForListCommand
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.pagination
import ru.yarsu.v1.serializers.TaskListSerializer

class TaskListHandler(
    private val taskList: List<TaskModel>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val workFlowWithTasks = WorkFlowWithTasks(taskList)

        // handle query parameters
        val page: String = request.uri.queries().findSingle("page") ?: "1"
        val recordsPerPage: String = request.uri.queries().findSingle("records-per-page") ?: "10"

        val taskListSerializer = TaskListSerializer()
        try {
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
            val result: List<TasksForListCommand> = pagination(workFlowWithTasks.getSortedTaskList(), page.toInt(), recordsPerPage.toInt())
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskListSerializer.taskList(result))
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskListSerializer.serializeError(e.message.toString()))
        }
    }
}
