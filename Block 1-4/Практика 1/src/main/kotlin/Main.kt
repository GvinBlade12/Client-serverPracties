import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() = runBlocking {

    val time = measureTimeMillis {

        val usersDeferred = async {
            try {
                delay(1800)
                randomFail("Users service")

                listOf("Петя", "Вася", "Колобок")
            } catch (e: Exception) {
                println("Ошибка при загрузке пользователей: ${e.message}")
                null
            }
        }

        val salesDeferred = async {
            try {
                delay(1200)
                randomFail("Sales service")

                mapOf("Продукт: молоко" to 10, "Продукт: кефир" to 5)
            } catch (e: Exception) {
                println("Ошибка при загрузке продаж: ${e.message}")
                null
            }
        }

        val weatherDeferred = async {
            try {
                delay(2500)
                randomFail("Weather service")

                listOf(
                    "Москва: -3°C",
                    "Лондон: 5°C",
                    "Париж: 7°C"
                )
            } catch (e: Exception) {
                println("Ошибка при получении погоды: ${e.message}")
                null
            }
        }

        val users = usersDeferred.await()
        val sales = salesDeferred.await()
        val weather = weatherDeferred.await()

        if (users != null && sales != null && weather != null) {
            println("\n=== Результаты ===")
            println("Пользователи: $users")
            println("Продажи: $sales")
            println("Погода: $weather")
        } else {
            println("\nНе удалось получить все данные.")
        }
    }

    println("\nОбщее время выполнения: $time мс")
}

// Функция для случайного падения
fun randomFail(serviceName: String) {
    if (Random.nextInt(0, 4) == 0) { // ~25% шанс ошибки
        throw RuntimeException("$serviceName недоступен")
    }
}