package ru.yarsu.taskworkflow

import java.time.LocalDate

class WorkFlowWithTasks {
    fun getTaskList(tasks: List<TaskModel>) : List<TaskModel>
    {
//      val filtredTasks: List<TaskModel> = tasks.filter { LocalDate.parse(it.registrationDateTime)}
        val sortedFilteredTasks = tasks.sortedBy { it.registrationDateTime }
        return sortedFilteredTasks
    }
    //Спарсить 3 и 4 - Начало задачи И Плановое окончание задачи
}
