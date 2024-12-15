package ru.yarsu

import com.beust.jcommander.Parameter
import com.beust.jcommander.Parameters

@Parameters()
open class Args {
    @Parameter(
        names = ["--tasks-file"],
        required = true,
        description = "Обязательный аргумент, принимает путь к csv файлу с данными",
    )
    var urlFile: String? = null

    @Parameter(
        names = ["--users-file"],
        required = true,
        description = "Имя файла с пользователями приложения",
    )
    var userFile: String? = null

    @Parameter(
        names = ["--port"],
        required = true,
        description = "Порт, по которому доступен веб-сервер",
    )
    var numberPort: Int? = null

    @Parameter(
        names = ["--categories-file"],
        required = true,
        description = "Имя файла со списком категорий",
    )
    var categoriesFile: String? = null
}
