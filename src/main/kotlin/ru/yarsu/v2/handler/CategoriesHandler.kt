package ru.yarsu.v2.handler

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.form
import org.http4k.routing.path
import ru.yarsu.Categories
import ru.yarsu.TaskModel
import ru.yarsu.User
import ru.yarsu.WorkFlowWithCategories
import ru.yarsu.v2.jsonResponseLens
import ru.yarsu.v2.utils.putCategory
import ru.yarsu.v2.utils.validateBodyCategory
import java.util.UUID

class CategoriesHandler(
    private val categoriesList: List<Categories>,
    private val userList: List<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val jsonResponse = jsonResponseLens<List<Map<String, Any?>>>()
        val categories = mutableListOf<Map<String, Any?>>()
        val sortedList = WorkFlowWithCategories(categoriesList).getSortedList()
        for (item in sortedList) {
            categories.add(
                mapOf<String, Any?>(
                    "Id" to item.id.toString(),
                    "Description" to item.description,
                    "Color" to item.color,
                    "Owner" to item.owner,
                    "OwnerName" to (userList.find { it.id == item.owner }?.login ?: "Общая"),
                ),
            )
        }
        return jsonResponse.invoke(categories, Status.OK)
    }
}

class EditCategory(
    private var taskList: MutableList<TaskModel>,
    private var categoriesList: MutableList<Categories>,
    private var userList: MutableList<User>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val categoryId: String = request.path("category-id") ?: return Response(Status.BAD_REQUEST)
        val jsonResponse = jsonResponseLens<Any>()

        val description = request.form("Description")

        val owner = request.form("Owner") // новый владелец категории

        val errors = validateBodyCategory(description, owner)

        if (errors.isNotEmpty()) {
            return jsonResponse.invoke(errors, Status.BAD_REQUEST)
        }

        val workFlowWithCategories = WorkFlowWithCategories(categoriesList)

        val uuidCategories: UUID =
            UUID.fromString(
                categoryId,
            )
        try {
            val category = workFlowWithCategories.getCategoryById(uuidCategories) // получили сам элемет

            val index = categoriesList.indexOfFirst { it.id == category.id } // получили индекс элемента в списке

            // val errorsOwners = validateOwnersTask(taskList, uuidCategories, userList)

//            if(errorsOwners.isNotEmpty()){return jsonResponse.invoke(errorsOwners, Status.FORBIDDEN)}

            val newCategory = putCategory(category, description!!, UUID.fromString(owner)) // сформировали новый элемент категории

            categoriesList[index] = newCategory

            // меняем автора всех задач
            for (item in taskList) {
                if (item.category == uuidCategories) {
                    item.author = UUID.fromString(owner)
                }
            }

            return Response(Status.NO_CONTENT)
        } catch (e: NullPointerException) {
            return jsonResponse.invoke(
                mutableMapOf("CategoryId" to uuidCategories.toString(), "Error" to e.message.toString()),
                Status.NOT_FOUND,
            )
        }
    }
}
