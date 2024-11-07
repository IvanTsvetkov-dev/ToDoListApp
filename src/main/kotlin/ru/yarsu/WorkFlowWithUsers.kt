package ru.yarsu

import java.util.UUID

class WorkFlowWithUsers(
    private val usersData: List<User>,
) {
    fun getUserByUUIDAuthor(uuid: UUID): User? = usersData.find { it.id == uuid }
}
