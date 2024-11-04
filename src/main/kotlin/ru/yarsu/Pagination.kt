package ru.yarsu

fun<T> pagination(listToPaginate: List<T> ,page: Int, recordsPerPage: Int) : List<T>{
    if(page < 1){
        throw IllegalArgumentException("Некорректное значение параметра page. Ожидается натуральное число, но получено $page")
    }
    if(recordsPerPage !in listOf(5, 10, 20, 50)){
        throw IllegalArgumentException("Некорректное значение параметра records-per-page. Ожидается 5 10 20 50, но получено $recordsPerPage")
    }
    if(page * recordsPerPage > listToPaginate.count()){
        return listOf()
    }
    return listToPaginate.drop(page-1).take(recordsPerPage)
}
