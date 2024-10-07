package ru.yarsu
import com.beust.jcommander.*;

@Parameters(separators = "=")
open class Args {
    @Parameter(names = ["--tasks-file"],
        required = true,
        description = "Обязательный аргумент, принимает путь к csv файлу с данными")
    var urlFile: String = ""
}

@Parameters(separators = "=", commandDescription = "Выводит информацию о задаче по её UUID")
class ShowTask : Args(){
    @Parameter(names = ["--task-id"],
        arity = 1,
        description = "Полная информация о задаче в формате JSON"
    )
    var taskID: String? = null
}

class TaskList : Args() {}

@Parameters(separators = "=", commandDescription = "Получить список задач для блока Матрицы Эйзенхауэра")
class ListEisenHower : Args() {
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

@Parameters(separators = "=", commandDescription = "Получить список задач, актуальных на момент времени")
class ListTime : Args() {
    @Parameter(names = ["--time"],
        arity = 1,
        description = "Обязательный параметр даты и времени в формате ISO",
        required = true
    )
    var time: String? = null
}

@Parameters(separators = "=", commandDescription = "Получить статистическую информацию о списке задач по дням недели")
class Statistic : Args() {
    @Parameter(names = ["--by-date"],
        arity = 1,
        description = "Обязательный параметр типа отображаемой статистики",
        required = true
    )
    var valueStatistic: String? = null
}

@Parameters(separators = "=", commandDescription = "Статистика в формате JSON содержащая пункты.")
class StatisticByHowReady{
    @Parameter(names = ["--tasks-for-statistic-file"],
        arity = 1,
        description = "Путь к файлу",
        required = true
    )
    var statisticByHowReadyFile: String = ""
}

