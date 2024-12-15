package ru.yarsu.v2.handler

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.path
import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithCategories
import ru.yarsu.WorkFlowWithTasks
import ru.yarsu.WorkFlowWithUsers
import ru.yarsu.v2.jsonResponseLens
import ru.yarsu.v2.utils.createErrorLog
import java.util.UUID

class UserShowHandler(
    var userList: MutableList<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val jsonResponse = jsonResponseLens<Any>()

        var totalUser = mutableListOf<MutableMap<String, String>>()

        val sortedListUser = userList.sortedWith(compareBy<User> { it.login })
        for (users in sortedListUser) {
            totalUser.add(
                mutableMapOf(
                    "Id" to users.id.toString(),
                    "Login" to users.login,
                    "RegistrationDateTime" to users.registrationDateTime,
                    "Email" to users.email,
                ),
            )
        }
        return jsonResponse.invoke(sortedListUser, Status.OK)
    }
}

class UsersHandler(
    private val taskList: MutableList<TaskModel>,
    private var userList: MutableList<User>,
    private val categoriesList: MutableList<Categories>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val userId: String = request.path("user-id") ?: return Response(Status.BAD_REQUEST)

        val jsonResponse = jsonResponseLens<Any>()

        val workFlowWithTasks = WorkFlowWithTasks(taskList)
        val workFlowWithCategories = WorkFlowWithCategories(categoriesList)
        val workFlowWithUsers = WorkFlowWithUsers(userList)

        try {
            val uuidUser = UUID.fromString(userId)

            val user = workFlowWithUsers.getUserByUUID(uuidUser)

            val errors = createErrorLog(taskList, categoriesList, uuidUser)

            val tasks = errors["Tasks"] ?: mutableListOf()
            val categories = errors["Categories"] ?: mutableListOf()

            if (tasks.isNotEmpty() || categories.isNotEmpty()) {
                return jsonResponse(errors, Status.FORBIDDEN)
            }

            userList.removeIf { it.id == user.id }

            for (i in userList.indices) {
                if (userList[i].id == uuidUser) {
                    userList.removeAt(i)
                    break
                }
            }

            return Response(Status.NO_CONTENT)
        } catch (e: NullPointerException) {
            return jsonResponse.invoke(mutableMapOf("UserId" to userId, "Error" to e.message.toString()), Status.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            return jsonResponse.invoke(
                mutableMapOf(
                    "Error" to "Некорректное значение переданного параметра id. Ожидается UUID, но получено $userId",
                ),
                Status.BAD_REQUEST,
            )
        }
    }
}
