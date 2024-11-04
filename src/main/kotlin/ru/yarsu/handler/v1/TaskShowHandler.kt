package ru.yarsu.handler.v1

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.TaskModel
import ru.yarsu.WorkFlowWithTasks
import java.io.StringWriter
import java.util.*


class TaskShowHandler(private val tasklist: List<TaskModel>) : HttpHandler{
    override fun invoke(request: Request) : Response {
        //обработка query параметров
        val taskId : String = request.path("task-id") ?: return Response(Status.NOT_FOUND)

        //создание класса
        val workFlowWithTasks = WorkFlowWithTasks(tasklist)

        //TODO handle potential with error uuid.fromString(taskId)

        val task = workFlowWithTasks.getTaskById(UUID.fromString(taskId))

        if(task == null){
            val errorMessage = StringWriter()

            val factory = JsonFactory()
            val outputGenerator = factory.createGenerator(errorMessage)

            val printer = DefaultPrettyPrinter()
            printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
            outputGenerator.prettyPrinter = printer

            with(outputGenerator){
                writeStartObject()

                writeFieldName("task-id")
                writeString(taskId)

                writeFieldName("error")
                writeString("Задача не найдена ")

                writeEndObject()
                close()
            }
            return Response(Status.NOT_FOUND).body(errorMessage.toString())
        }else
            return Response(Status.OK).body(task)
    }
}
