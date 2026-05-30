package com.example.nobelapi.data.database
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init() {
        // Читаем переменные окружения
        val dbHost = System.getenv("DB_HOST") ?: error("DB_HOST not set")
        val dbPort = System.getenv("DB_PORT") ?: error("DB_PORT not set")
        val dbName = System.getenv("DB_NAME") ?: error("DB_NAME not set")
        val dbUser = System.getenv("DB_USER") ?: error("DB_USER not set")
        val dbPassword = System.getenv("DB_PASSWORD") ?: error("DB_PASSWORD not set")
        val sslMode = System.getenv("DB_SSL_MODE") ?: "require"

        val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName?sslmode=$sslMode"

        println("Connecting to: jdbc:postgresql://$dbHost:$dbPort/$dbName?sslmode=$sslMode")

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.username = dbUser
            this.password = dbPassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 300000
            maxLifetime = 1800000
            connectionTimeout = 30000
            isAutoCommit = false
        }

        dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        transaction {
            SchemaUtils.create(
                UsersTable,
                PrizesTable,
                LaureatesTable,
                UserPrizesTable
            )
        }
        println("Database connected successfully!")
    }
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}