package com.example.nobelapi.data.repository

import com.example.nobelapi.data.database.DatabaseFactory
import com.example.nobelapi.data.database.LaureatesTable
import com.example.nobelapi.data.database.PrizesTable
import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.NobelPrizeRepository
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class NobelPrizeRepositoryImpl : NobelPrizeRepository {

    override suspend fun getAllPrizes(): List<NobelPrize> = DatabaseFactory.dbQuery {
        PrizesTable.selectAll().map { row ->
            val prizeId = row[PrizesTable.id]
            val laureates = LaureatesTable.select { LaureatesTable.prizeId eq prizeId }.map { laureateRow ->
                Laureate(
                    id = laureateRow[LaureatesTable.laureateId],
                    fullName = laureateRow[LaureatesTable.fullName],
                    motivation = laureateRow[LaureatesTable.motivation] ?: ""
                )
            }
            NobelPrize(
                awardYear = row[PrizesTable.awardYear],
                category = row[PrizesTable.category],
                prizeAmount = 10000000,
                laureates = laureates
            )
        }
    }

    override suspend fun getPrizeByYearAndCategory(year: String, category: String): NobelPrize? = DatabaseFactory.dbQuery {
        val prizeRow = PrizesTable.select {
            (PrizesTable.awardYear eq year) and (PrizesTable.category eq category)
        }.singleOrNull() ?: return@dbQuery null

        val laureates = LaureatesTable.select { LaureatesTable.prizeId eq prizeRow[PrizesTable.id] }.map { laureateRow ->
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

    override suspend fun savePrize(prize: NobelPrize): Int = DatabaseFactory.dbQuery {
        val prizeId = PrizesTable.insert {
            it[PrizesTable.prizeId] = "${prize.awardYear}-${prize.category}"
            it[PrizesTable.awardYear] = prize.awardYear
            it[PrizesTable.category] = prize.category
            it[PrizesTable.fullName] = "${prize.category.uppercase()} Prize ${prize.awardYear}"
            it[PrizesTable.motivation] = "Nobel Prize in ${prize.category}"
            it[PrizesTable.detailLink] = "https://nobelprize.org/prizes/${prize.category}/${prize.awardYear}/"
        } get PrizesTable.id

        prize.laureates.forEach { laureate ->
            LaureatesTable.insert {
                it[LaureatesTable.prizeId] = prizeId
                it[LaureatesTable.laureateId] = laureate.id
                it[LaureatesTable.fullName] = laureate.fullName
                it[LaureatesTable.motivation] = laureate.motivation
                it[LaureatesTable.portion] = "1"
            }
        }
        prizeId
    }

    override suspend fun getPrizeById(id: Int): NobelPrize? = DatabaseFactory.dbQuery {
        val prizeRow = PrizesTable.select { PrizesTable.id eq id }.singleOrNull() ?: return@dbQuery null
        val laureates = LaureatesTable.select { LaureatesTable.prizeId eq id }.map { laureateRow ->
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

    override suspend fun getLaureatesByPrize(year: String, category: String): List<Laureate>? = DatabaseFactory.dbQuery {
        getPrizeByYearAndCategory(year, category)?.laureates
    }

    override suspend fun prizeExists(prizeId: String): Boolean = DatabaseFactory.dbQuery {
        PrizesTable.select { PrizesTable.prizeId eq prizeId }.count() > 0
    }

    override suspend fun savePrizeFromApi(prizeDto: com.example.nobelapi.data.api.NobelPrizeApiDto): Int = DatabaseFactory.dbQuery {
        val prizeId = PrizesTable.insert {
            it[PrizesTable.prizeId] = "${prizeDto.awardYear}-${prizeDto.category.en}"
            it[PrizesTable.awardYear] = prizeDto.awardYear
            it[PrizesTable.category] = prizeDto.category.en
            it[PrizesTable.fullName] = "The Nobel Prize in ${prizeDto.category.en} ${prizeDto.awardYear}"
            it[PrizesTable.motivation] = "Nobel Prize in ${prizeDto.category.en}"
            it[PrizesTable.detailLink] = "https://nobelprize.org/prizes/${prizeDto.category.en}/${prizeDto.awardYear}/"
        } get PrizesTable.id

        prizeDto.laureates?.forEach { laureate ->
            LaureatesTable.insert {
                it[LaureatesTable.prizeId] = prizeId
                it[LaureatesTable.laureateId] = laureate.id
                it[LaureatesTable.fullName] = laureate.fullName?.en ?: "Unknown"
                it[LaureatesTable.motivation] = laureate.motivation?.en ?: ""
                it[LaureatesTable.portion] = "1"
            }
        }
        prizeId
    }
}