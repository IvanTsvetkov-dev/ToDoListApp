package ru.yarsu.handler.v1

import org.http4k.core.*
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.serializers.TaskListSerializer

class TaskListHandler(private val taskList: List<TaskModel>) : HttpHandler{
    override fun invoke(request: Request): Response {
        val workFlowWithTasks = WorkFlowWithTasks(taskList)

        //handle query parameters
        val page: String = request.uri.queries().findSingle("page") ?: "1"
        val recordsPerPage: String = request.uri.queries().findSingle("records-per-page") ?: "10"

        val taskListSerializer = TaskListSerializer()
        try{
            val result = workFlowWithTasks.getSortedTaskList(page.toInt(), recordsPerPage.toInt())
            return Response(Status.OK).body(taskListSerializer.taskList(result))
        } catch (e: IllegalArgumentException){
            return Response(Status.BAD_REQUEST).body(taskListSerializer.serializeError(e.message.toString()))
        }
    }
}
