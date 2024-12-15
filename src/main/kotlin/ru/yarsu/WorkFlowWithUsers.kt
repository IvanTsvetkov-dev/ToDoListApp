package ru.yarsu

import java.util.UUID

class WorkFlowWithUsers(
    private val usersData: List<User>,
) {
    fun getUserByUUID(uuid: UUID): User {
        val userBy = usersData.find { it.id == uuid }
        if (userBy == null) {
            throw NullPointerException("Пользователь не найден")
        }
        return userBy
    }
}
