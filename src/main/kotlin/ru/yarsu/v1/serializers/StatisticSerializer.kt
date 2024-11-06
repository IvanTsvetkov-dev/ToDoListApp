package ru.yarsu.v1.serializers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import ru.yarsu.ValuesStatistic
import java.io.StringWriter

class StatisticSerializer : BaseSerializer() {
    fun statisticSerializer(dayCount: Map<String, Int>,typeStatistic: ValuesStatistic) : String{
        val stringWriter = StringWriter()
        val factory = JsonFactory()
        val outputGenerator: JsonGenerator = factory.createGenerator(stringWriter)
        val printer = DefaultPrettyPrinter()
        printer.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        outputGenerator.prettyPrinter = printer
        with(outputGenerator) {
            writeStartObject()

            writeFieldName(when(typeStatistic){
                ValuesStatistic.REGISTRATION -> "statisticByRegistrationDateTime"
                ValuesStatistic.START -> "statisticByStartDateTime"
                ValuesStatistic.END -> "statisticByEndDateTime"
            })

            writeStartObject()
            for (day in listOf(
                "Понедельник", "Вторник", "Среда", "Четверг",
                "Пятница", "Суббота", "Воскресенье", "Не заполнено"
            )) {
                dayCount[day]?.let { count ->
                    writeFieldName(day)
                    writeNumber(count)
                }
            }
            writeEndObject()

            writeEndObject()
            close()
        }
        return stringWriter.toString()
    }
}
