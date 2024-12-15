package ru.yarsu

import java.util.UUID

class WorkFlowWithCategories(
    private val categoriesData: List<Categories>,
) {
    fun getSortedList(): List<Categories> = categoriesData.sortedWith(compareBy<Categories> { it.description }.thenBy { it.id })

    fun getCategoryById(id: UUID): Categories {
        val categoriesById = categoriesData.find { it.id == id }
        if (categoriesById == null) {
            throw NullPointerException("Категория не найдена")
        }
        return categoriesById
    }
}
