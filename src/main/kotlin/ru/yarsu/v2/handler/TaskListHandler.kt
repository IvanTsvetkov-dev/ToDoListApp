package ru.yarsu.v2.handler

import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.findSingle
import org.http4k.core.queries
import org.http4k.format.Jackson.auto
import org.http4k.lens.LensFailure
import org.http4k.lens.contentType
import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.TasksForListCommand
import ru.yarsu.User
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.pagination
import ru.yarsu.v2.jsonResponseLens
import ru.yarsu.v2.serializers.TaskListSerializer
import ru.yarsu.v2.utils.createTask
import ru.yarsu.v2.utils.validateBody
import ru.yarsu.v2.utils.validateMathingAuthorCategory
import java.util.UUID

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

val jsonBodyLens = Body.auto<Map<String, Any>>().toLens()

class AddNewTaskHandler(
    private var taskList: MutableList<TaskModel>,
    private var userList: MutableList<User>,
    private var categoryList: MutableList<Categories>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val jsonResponse = jsonResponseLens<Map<String, Any>>()
        try {
            val body = jsonBodyLens(request)

            val listError = validateBody(body, userList)

            if (listError.isNotEmpty()) {
                return jsonResponse.invoke(listError, Status.BAD_REQUEST)
            }

            val newTask =
                createTask(
                    body,
                    body["Title"].toString(),
                    UUID.fromString(body["Author"].toString()),
                    UUID.fromString(body["Category"].toString()),
                )

            val matchingAuthorCategory =
                validateMathingAuthorCategory(
                    categoryList,
                    UUID.fromString(body["Author"].toString()),
                    UUID.fromString(body["Category"].toString()),
                )

            if (matchingAuthorCategory.isNotEmpty()) {
                return jsonResponse.invoke(matchingAuthorCategory, Status.FORBIDDEN)
            }

            taskList.add(newTask)

            return jsonResponse(mapOf("Id" to newTask.id), Status.CREATED)
        } catch (e: LensFailure) {
            return jsonResponse.invoke(
                mapOf("Value" to request.bodyString(), "Error" to "Missing a name for object member."),
                Status.BAD_REQUEST,
            )
        }
    }
}
