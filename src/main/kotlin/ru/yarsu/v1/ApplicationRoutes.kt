package ru.yarsu.v1

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.v1.handler.EisenhowerListHandler
import ru.yarsu.v1.handler.ListTimeHandler
import ru.yarsu.v1.handler.PingHandler
import ru.yarsu.v1.handler.StatisticHandler
import ru.yarsu.v1.handler.TaskHandler
import ru.yarsu.v1.handler.TaskListHandler
import ru.yarsu.v1.handler.TaskShowHandler

fun applicationRoutes(
    taskList: List<TaskModel>,
    userList: List<User>,
): RoutingHttpHandler {
    val app =
        routes(
            "/ping" bind Method.GET to PingHandler(),
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
