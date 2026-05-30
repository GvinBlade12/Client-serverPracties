package com.example.nobelapi.data.repository

import com.example.nobelapi.data.database.DatabaseFactory
import com.example.nobelapi.data.database.LaureatesTable
import com.example.nobelapi.data.database.PrizesTable
import com.example.nobelapi.data.database.UserPrizesTable
import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.UserPrizeRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.LocalDateTime

class UserPrizeRepositoryImpl : UserPrizeRepository {

    override suspend fun getUserFavorites(userId: Int): List<NobelPrize> = DatabaseFactory.dbQuery {
        UserPrizesTable.select { UserPrizesTable.userId eq userId }.map { row ->
            val prizeId = row[UserPrizesTable.prizeId]
            val prizeRow = PrizesTable.select { PrizesTable.id eq prizeId }.single()
            val laureates = LaureatesTable.select { LaureatesTable.prizeId eq prizeId }.map { laureateRow ->
                Laureate(
                    id = laureateRow[LaureatesTable.laureateId],
                    fullName = laureateRow[LaureatesTable.fullName],
                    motivation = laureateRow[LaureatesTable.motivation] ?: ""
                )
            }
            NobelPrize(
                awardYear = prizeRow[PrizesTable.awardYear],
                category = prizeRow[PrizesTable.category],
                prizeAmount = 10000000,
                laureates = laureates
            )
        }
    }

    override suspend fun addFavorite(userId: Int, prizeId: Int): Boolean = DatabaseFactory.dbQuery {
        UserPrizesTable.insert {
            it[UserPrizesTable.userId] = userId
            it[UserPrizesTable.prizeId] = prizeId
            it[UserPrizesTable.addedAt] = LocalDateTime.now()
        }
        true
    }

    override suspend fun removeFavorite(userId: Int, prizeId: Int): Boolean = DatabaseFactory.dbQuery {
        val condition = (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId)
        UserPrizesTable.deleteWhere { condition } > 0
    }

    override suspend fun isFavorite(userId: Int, prizeId: Int): Boolean = DatabaseFactory.dbQuery {
        val condition = (UserPrizesTable.userId eq userId) and (UserPrizesTable.prizeId eq prizeId)
        UserPrizesTable.select(condition).count() > 0
    }
}