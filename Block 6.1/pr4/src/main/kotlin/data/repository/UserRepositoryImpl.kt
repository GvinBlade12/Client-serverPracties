package com.example.nobelapi.data.repository
import com.example.nobelapi.data.database.DatabaseFactory
import com.example.nobelapi.data.database.UsersTable
import com.example.nobelapi.domain.model.User
import com.example.nobelapi.domain.repository.UserRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class UserRepositoryImpl : UserRepository {

    override suspend fun findByUsername(username: String): User? = DatabaseFactory.dbQuery {
        UsersTable.select { UsersTable.username eq username }
            .map { row ->
                User(
                    id = row[UsersTable.id],
                    username = row[UsersTable.username],
                    passwordHash = row[UsersTable.passwordHash],
                    role = row[UsersTable.role]
                )
            }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): User? = DatabaseFactory.dbQuery {
        UsersTable.select { UsersTable.id eq id }
            .map { row ->
                User(
                    id = row[UsersTable.id],
                    username = row[UsersTable.username],
                    passwordHash = row[UsersTable.passwordHash],
                    role = row[UsersTable.role]
                )
            }
            .singleOrNull()
    }

    override suspend fun createUser(username: String, passwordHash: String, role: String): User = DatabaseFactory.dbQuery {
        val id = UsersTable.insert {
            it[UsersTable.username] = username
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.role] = role
        } get UsersTable.id
        User(id, username, passwordHash, role)
    }

    override suspend fun updateUserRole(userId: Int, role: String): Boolean = DatabaseFactory.dbQuery {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[UsersTable.role] = role
        } > 0
    }
}