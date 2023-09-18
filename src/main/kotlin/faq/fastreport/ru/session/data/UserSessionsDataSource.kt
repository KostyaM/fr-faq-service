package faq.fastreport.ru.session.data

import faq.fastreport.ru.db.faq.FaqTreeNodes
import faq.fastreport.ru.db.session.UserSession
import faq.fastreport.ru.db.session.UserSessions
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.UUID

class UserSessionsDataSource(private val database: Database) {
    fun isUserExists(userId: UUID): Boolean {
        return database.from(UserSessions).select()
            .where { UserSessions.userId eq userId }
            .limit(1)
            .totalRecordsInAllPages > 0
    }

    fun createSession(userSession: UserSessionDto) {
        database.insert(UserSessions) {
            set(UserSessions.userId, userSession.userId)
            set(UserSessions.stage, userSession.stages.toTypedArray())
        }
    }

    fun getSession(userId: UUID): UserSessionDto? {
        return database.from(UserSessions).select()
            .where { UserSessions.userId eq userId }
            .limit(1)
            .map { row ->
                UserSessionDto(
                    id = row[UserSessions.id],
                    userId = userId,
                    stages = row[UserSessions.stage]?.mapNotNull { it } ?: emptyList()
                )
            }.firstOrNull()
    }

    fun setStages(userId: UUID, stages: List<Int>) {
        database.update(UserSessions) {
            set(UserSessions.stage, stages.toTypedArray())
            where { UserSessions.userId eq userId }
        }
    }
}