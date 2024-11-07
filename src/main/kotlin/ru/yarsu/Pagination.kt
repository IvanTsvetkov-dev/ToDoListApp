package ru.yarsu

fun <T> pagination(
    list: List<T>,
    page: Int,
    recordsPerPage: Int,
): List<T> {
    if (page < 1) {
        throw IllegalArgumentException("Некорректное значение параметра page. Ожидается натуральное число, но получено $page")
    }
    if (recordsPerPage !in listOf(5, 10, 20, 50)) {
        throw IllegalArgumentException(
            "Некорректное значение параметра records-per-page.Ожидалось enum 5, 10, 20, 50, но получено $recordsPerPage",
        )
    }

    val totalPaginateList = mutableListOf<T>()

    val startIndex = (page - 1) * recordsPerPage

    var totalRecordsPerPage = recordsPerPage

    for (i in list.drop(startIndex)) {
        totalRecordsPerPage -= 1
        totalPaginateList.add(i)
        if (totalRecordsPerPage == 0) {
            break
        }
    }

    return totalPaginateList
}
