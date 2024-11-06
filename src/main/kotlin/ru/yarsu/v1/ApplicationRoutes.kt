package ru.yarsu.v1

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.v1.handler.*

fun applicationRoutes(
    taskList: List<TaskModel>,
    userList: List<User>,
): RoutingHttpHandler {
    // routes является http обработчиком типа RoutingHttpHandler.
    val app =
        routes( // bind возвращает PathMethod,связывая "uri" с http методом,
            "/ping" bind Method.GET to PingHandler(), // связывает строку с методом по его обработке
            "/v1" bind
                routes(
                    "/list-tasks" bind Method.GET to TaskListHandler(taskList),
                    "/task/{task-id}" bind Method.GET to TaskShowHandler(taskList, userList),
                    "/task" bind Method.GET to TaskHandler(),
                    "/list-eisenhower" bind Method.GET to EisenhowerListHandler(taskList),
                    "/list-time" bind Method.GET to ListTimeHandler(taskList),
                    "/statistic" bind Method.GET to StatisticHandler(taskList),
                ),
        )
    return app
}
