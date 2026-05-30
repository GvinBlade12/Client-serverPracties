package com.example.nobelapi.data.database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50).default("user")

    override val primaryKey = PrimaryKey(id)
}

object PrizesTable : Table("prizes") {
    val id = integer("id").autoIncrement()
    val prizeId = varchar("prize_id", 100).uniqueIndex()
    val awardYear = varchar("award_year", 10)
    val category = varchar("category", 50)
    val fullName = varchar("full_name", 255).nullable()
    val motivation = text("motivation").nullable()
    val detailLink = varchar("detail_link", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

object LaureatesTable : Table("laureates") {
    val id = integer("id").autoIncrement()
    val prizeId = integer("prize_id").references(PrizesTable.id)
    val laureateId = varchar("laureate_id", 100)
    val fullName = varchar("full_name", 255)
    val portion = varchar("portion", 10)
    val motivation = text("motivation").nullable()
    val portraitUrl = varchar("portrait_url", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

object UserPrizesTable : Table("user_prizes") {
    val userId = integer("user_id").references(UsersTable.id)
    val prizeId = integer("prize_id").references(PrizesTable.id)
    val addedAt = datetime("added_at")
    override val primaryKey = PrimaryKey(userId, prizeId)
}