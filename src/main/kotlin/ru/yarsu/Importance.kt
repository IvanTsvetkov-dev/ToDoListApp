package ru.yarsu

enum class Importance(var importance: String) {
    VERYLOW("очень низкий"),
    LOW("низкий"),
    DEFAULT("обычный"),
    HIGH("высокий"),
    VERYHIGH("очень высокий"),
    CRITICAL("критический")

}
fun parseImportance(importanceString: String): String {
    for(importance in Importance.entries){
        if(importanceString == importance.importance){
            return importanceString
        }
    }
    throw IllegalArgumentException("Неизвестный Importance. Может быть только очень низкий, низкий, обычный, высокий, очень высокий, критический")
}
