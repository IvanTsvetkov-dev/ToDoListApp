package ru.yarsu.web.routes

import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.TaskModel
import ru.yarsu.handler.v1.PingHandler
import ru.yarsu.handler.v1.TaskListHandler
import ru.yarsu.handler.v1.TaskShowHandler

fun applicationRoutes(taskList: List<TaskModel>) : RoutingHttpHandler{
    //routes является http обработчиком типа RoutingHttpHandler.
    val app = routes( //bind возвращает PathMethod,связывая "uri" с http методом,
        "/" bind Method.GET to { request: Request ->  Response(Status.OK).body("Its ToDoListApp")},
        "/ping" bind Method.GET to PingHandler(), //связывает строку с методом по его обработке
        "/v1" bind routes(
            "/list-tasks" bind Method.GET to TaskListHandler(taskList),
            "/task/{task-id}" bind Method.GET to TaskShowHandler(taskList)
        )
    )
    return app
}
