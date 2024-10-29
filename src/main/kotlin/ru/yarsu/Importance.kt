package ru.yarsu

enum class Importance(var importance: String) {
    VERYLOW("очень низкий"),
    LOW("низкий"),
    DEFAULT("обычный"),
    HIGH("высокий"),
    VERYHIGH("очень высокий"),
    CRITICAL("критический")

}
fun parseImportance(importanceString: String): Importance {
    return when(importanceString){
        "очень низкий" -> Importance.VERYLOW
        "низкий" -> Importance.LOW
        "обычный" -> Importance.DEFAULT
        "высокий" -> Importance.HIGH
        "очень высокий" -> Importance.VERYHIGH
        "критический" -> Importance.CRITICAL
        else -> throw IllegalArgumentException("Неизвестный Importance. Может быть только очень низкий, низкий, обычный, высокий, очень высокий, критический")

    }
}
