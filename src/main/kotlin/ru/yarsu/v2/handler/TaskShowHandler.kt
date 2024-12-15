package ru.yarsu.v2.handler

import org.http4k.core.ContentType
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.LensFailure
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.WorkFlowWithUsers
import ru.yarsu.v2.jsonResponseLens
import ru.yarsu.v2.serializers.TaskShowSerializer
import ru.yarsu.v2.utils.putTask
import ru.yarsu.v2.utils.validateBody
import java.util.UUID

class TaskShowHandler(
    private val tasklist: List<TaskModel>,
    private val userList: List<User>,
    private val categoryList: List<Categories>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val taskId: String = request.path("task-id") ?: return Response(Status.BAD_REQUEST)

        // создание класса предметной области
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val workFlowWithUsers = WorkFlowWithUsers(userList)

        val taskShowSerializer = TaskShowSerializer()

        try {
            val uuid = UUID.fromString(taskId)
            val task = workFlowWithTasks.getTaskById(uuid)
            val user = workFlowWithUsers.getUserByUUID(task.author)
            val category = categoryList.find { it.id == task.category }?.description ?: "ошибка"

            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeeTask(task, user.email, category))
        } catch (e: NullPointerException) {
            return Response(Status.NOT_FOUND)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeNotFoundTask(taskId, e.message.toString()))
        } catch (e: IllegalArgumentException) {
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(
                    taskShowSerializer.serializeError(
                        "Некорректный идентификатор задачи. Для параметра task-id ожидается UUID, но получено значение «$taskId»",
                    ),
                )
        }
    }
}

class TaskShowPutHandler(
    private val tasklist: MutableList<TaskModel>,
    private val userList: MutableList<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val taskId: String = request.path("task-id") ?: return Response(Status.BAD_REQUEST)
        val jsonResponse = jsonResponseLens<Map<String, Any>>()
        try {
            val body = jsonBodyLens(request)

            val listError = validateBody(body, userList)

            if (listError.isNotEmpty()) {
                return jsonResponse.invoke(listError, Status.BAD_REQUEST)
            }

            val workFlowWithTasks = WorkFlowWithTasks(tasklist)
            val workFlowWithUsers = WorkFlowWithUsers(userList)
            val taskShowSerializer = TaskShowSerializer()
            try {
                val uuid = UUID.fromString(taskId)
                val task = workFlowWithTasks.getTaskById(uuid)
                val user = workFlowWithUsers.getUserByUUID(task.author)

                val newTask =
                    putTask(
                        body,
                        body["Title"].toString(),
                        UUID.fromString(body["Author"].toString()),
                        UUID.fromString(body["Category"].toString()),
                        task,
                    )

                val index = tasklist.indexOfFirst { it.id == task.id }

                tasklist[index] = newTask

                return Response(Status.NO_CONTENT)
            } catch (e: NullPointerException) {
                return Response(Status.NOT_FOUND)
                    .contentType(ContentType.APPLICATION_JSON)
                    .body(taskShowSerializer.serializeNotFoundTask(taskId, e.message.toString()))
            } catch (e: IllegalArgumentException) {
                return Response(Status.BAD_REQUEST)
                    .contentType(ContentType.APPLICATION_JSON)
                    .body(
                        taskShowSerializer.serializeError(
                            "Некорректный идентификатор задачи. Для параметра task-id ожидается UUID, но получено значение «$taskId»",
                        ),
                    )
            }

//            val newTask = createTask(body, body["Title"].toString(), UUID.fromString(body["Author"].toString()), UUID.fromString(body["Category"].toString()))
//
//            return jsonResponse(mapOf("Id" to newTask.id), Status.CREATED)
        } catch (e: LensFailure) {
            return jsonResponse.invoke(
                mapOf("Value" to request.bodyString(), "Error" to "Missing a name for object member."),
                Status.BAD_REQUEST,
            )
        }
    }
}
