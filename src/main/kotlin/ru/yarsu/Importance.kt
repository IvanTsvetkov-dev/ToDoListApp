package ru.yarsu

enum class Importance(
    var importance: String,
) {
    VERYLOW("очень низкий"),
    LOW("низкий"),
    DEFAULT("обычный"),
    HIGH("высокий"),
    VERYHIGH("очень высокий"),
    CRITICAL("критический"),
}

fun parseImportance(importanceString: String): Importance =
    when (importanceString) {
        "очень низкий" -> Importance.VERYLOW
        "низкий" -> Importance.LOW
        "обычный" -> Importance.DEFAULT
        "высокий" -> Importance.HIGH
        "очень высокий" -> Importance.VERYHIGH
        "критический" -> Importance.CRITICAL
        else -> throw IllegalArgumentException(
            "Неизвестный Importance. Может быть только очень низкий, низкий, обычный, высокий, очень высокий, критический",
        )
    }

fun getImportanceRank(importance: String): Int =
    when (importance) {
        "критический" -> 6
        "очень высокий" -> 5
        "высокий" -> 4
        "обычный" -> 3
        "низкий" -> 2
        "очень низкий" -> 1
        else -> -1
    }
