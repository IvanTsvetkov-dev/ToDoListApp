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
            "Некорректное значение параметра records-per-page. Ожидается 5 10 20 50, но получено $recordsPerPage",
        )
    }

    val startIndex = recordsPerPage * (page - 1)

    val totalList = mutableListOf<T>()

    var totalRecordsPerPage = recordsPerPage
    for (i in list.drop(startIndex)) {
        totalRecordsPerPage -= 1
        totalList.add(i)
        if (totalRecordsPerPage == 0) {
            break
        }
    }
    return totalList
}
