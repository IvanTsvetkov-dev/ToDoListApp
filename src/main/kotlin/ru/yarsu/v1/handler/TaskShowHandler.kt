package ru.yarsu.v1.handler

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.http4k.core.*
import org.http4k.lens.contentType
import org.http4k.routing.path
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.WorkFlowWithUsers
import ru.yarsu.v1.serializers.TaskShowSerializer
import java.io.StringWriter
import java.util.*


class TaskShowHandler(private val tasklist: List<TaskModel>,
    private val userList: List<User>) : HttpHandler{
    override fun invoke(request: Request) : Response {
        val taskId : String = request.path("task-id") ?: return Response(Status.BAD_REQUEST)

        //создание класса предметной области
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)
        val workFlowWithUsers = WorkFlowWithUsers(userList)

        val taskShowSerializer = TaskShowSerializer()

        //TODO handle /v1/task/ -> 400 "error": "Отсутствует обязательный параметр task-id"
        try {
            val uuid = UUID.fromString(taskId)
            val task = workFlowWithTasks.getTaskById(uuid)
            val user = workFlowWithUsers.getUserByUUIDAuthor(task.author)
            return Response(Status.OK)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeeTask(task, user?.email))
        }catch (e: NullPointerException){
            return Response(Status.NOT_FOUND)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeNotFoundTask(taskId, e.message.toString()))
        }catch (e: IllegalArgumentException){
            return Response(Status.BAD_REQUEST)
                .contentType(ContentType.APPLICATION_JSON)
                .body(taskShowSerializer.serializeError("Некорректный идентификатор задачи. Для параметра task-id ожидается UUID, но получено значение «$taskId»"))
        }
    }
}
