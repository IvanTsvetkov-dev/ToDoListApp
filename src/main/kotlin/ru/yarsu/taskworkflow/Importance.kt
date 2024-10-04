package ru.yarsu.taskworkflow

enum class Importance(val importance: String) {
    VERYLOW("очень низкий"),
    LOW("низкий"),
    DEFAULT("обычный"),
    HIGH("высокий"),
    VERYHIGH("очень высокий"),
    CRITICAL("критический")

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
