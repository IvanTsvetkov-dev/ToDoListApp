package ru.yarsu.v2

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.v1.handler.EisenhowerListHandler
import ru.yarsu.v1.handler.ListTimeHandler
import ru.yarsu.v1.handler.StatisticHandler
import ru.yarsu.v2.handler.AddNewTaskHandler
import ru.yarsu.v2.handler.CategoriesHandler
import ru.yarsu.v2.handler.EditCategory
import ru.yarsu.v2.handler.PingHandler
import ru.yarsu.v2.handler.TaskListHandler
import ru.yarsu.v2.handler.TaskShowHandler
import ru.yarsu.v2.handler.TaskShowPutHandler
import ru.yarsu.v2.handler.UserShowHandler
import ru.yarsu.v2.handler.UsersHandler

fun applicationRoutes(
    taskList: MutableList<TaskModel>,
    userList: MutableList<User>,
    categoriesList: MutableList<Categories>,
): RoutingHttpHandler {
    val app =
        routes(
            "/ping" bind Method.GET to PingHandler(),
            "/v2" bind
                routes(
                    "/tasks" bind Method.GET to TaskListHandler(taskList),
                    "/tasks" bind Method.POST to AddNewTaskHandler(taskList, userList, categoriesList),
                    "/tasks/eisenhower" bind Method.GET to EisenhowerListHandler(taskList),
                    "/tasks/by-time" bind Method.GET to ListTimeHandler(taskList),
                    "/tasks/statistics" bind Method.GET to StatisticHandler(taskList),
                    "/tasks/{task-id}" bind Method.GET to TaskShowHandler(taskList, userList, categoriesList),
                    "/tasks/{task-id}" bind Method.PUT to TaskShowPutHandler(taskList, userList),
                    "/categories" bind Method.GET to CategoriesHandler(categoriesList, userList),
                    "/categories/{category-id}" bind Method.PUT to EditCategory(taskList, categoriesList, userList),
                    "/users" bind Method.GET to UserShowHandler(userList),
                    "/users/{user-id}" bind Method.DELETE to UsersHandler(taskList, userList, categoriesList),
                ),
        )
    return app
}
