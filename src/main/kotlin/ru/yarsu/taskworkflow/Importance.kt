package ru.yarsu.taskworkflow

enum class Importance(val importance: String) {
    VERYLOW("Очень низкий"),
    LOW("Низкий"),
    DEFAULT("Обычный"),
    HIGH("Высокий"),
    VERYHIGH("Очень Высокий"),
    CRITICAL("Критический")

}
