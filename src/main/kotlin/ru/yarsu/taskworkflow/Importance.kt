package ru.yarsu.taskworkflow

enum class Importance(val importance: String, val order: Int) {
    VERYLOW("очень низкий", 0),
    LOW("низкий", 1),
    DEFAULT("обычный", 2),
    HIGH("высокий", 3),
    VERYHIGH("очень высокий", 4),
    CRITICAL("критический", 5)

}
fun parseImportance(importanceString: String): Importance {
    return when (importanceString.lowercase()) {
        "очень низкий" -> Importance.VERYLOW
        "низкий" -> Importance.LOW
        "обычный" -> Importance.DEFAULT
        "высокий" -> Importance.HIGH
        "очень высокий" -> Importance.VERYHIGH
        "критический" -> Importance.CRITICAL
        else -> throw IllegalArgumentException("Unknown importance: $importanceString")
    }
}
