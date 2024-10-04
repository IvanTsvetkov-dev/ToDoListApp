package ru.yarsu
import com.beust.jcommander.*;

@Parameters(separators = "=")
class Args {
    @Parameter(names = ["--tasks-file"],
        required = true,
        description = "The csv file")
    var urlFile: String = ""
}

@Parameters(separators = "=")
class ShowTask{
    @Parameter(names = ["--task-id"],
        arity = 1,
        description = "Полная информация о задаче в формате JSON"
    )
    var taskID: String? = null
}

class TaskList {}

@Parameters(separators = "=")
class ListEisenHower{
    @Parameter(names = ["--important"],
        arity = 1,
        description = "true – задачи с приоритетом «высокий», «очень высокий», «критический» false – задачи с приоритетом «очень низкий», «низкий», «обычный»"
    )
    var important: Boolean? = null

    @Parameter(names = ["--urgent"],
        arity = 1,
        description = "Необязательный параметр срочности."
        )
    var urgent: Boolean? = null
}

@Parameters(separators = "=")
class ListTime{
    @Parameter(names = ["--time"],
        arity = 1,
        description = "Обязательный параметр даты и времени в формате ISO",
        required = true
    )
    var time: String? = null
}

@Parameters(separators = "=")
class Statistic{
    @Parameter(names = ["--by-date"],
        arity = 1,
        description = "Обязательный параметр типа отображаемой статистики",
        required = true
    )
    var valueStatistic: String? = null
}

enum class ValuesStatistic(val type: String){
    REGISTRATION(type = "registration"),
    START(type = "start"),
    END(type = "end")
}

fun parseValuesStatistic(type: String) : ValuesStatistic{
    return when (type.lowercase()) {
        "registration" -> ValuesStatistic.REGISTRATION
        "start" -> ValuesStatistic.START
        "end" -> ValuesStatistic.END
        else -> throw IllegalArgumentException("Unknown importance: $type")
    }
}
