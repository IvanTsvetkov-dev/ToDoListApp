package ru.yarsu

enum class ValuesStatistic(val type: String){
    REGISTRATION(type = "registration"),
    START(type = "start"),
    END(type = "end")
}

fun parseValuesStatistic(type: String) : ValuesStatistic {
    return when (type.lowercase()) {
        "registration" -> ValuesStatistic.REGISTRATION
        "start" -> ValuesStatistic.START
        "end" -> ValuesStatistic.END
        else -> throw IllegalArgumentException("Некорректное значение типа статистики. Для параметра by-date ожидается значение типа статистики, но получено $type")
    }
}
