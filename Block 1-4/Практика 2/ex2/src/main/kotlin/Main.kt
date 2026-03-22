import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest

fun main() = runBlocking {

    val directoryPath = "Папка или путь к ней" // Я НЕ буду убивать свой диск!!!
    val timeoutMillis = 5000L       // 5 секунд до таймаута

    val result = withTimeoutOrNull(timeoutMillis) {
        findDuplicates(directoryPath)
    }

    if (result == null) {
        println("Поиск прерван по таймауту")
    } else {
        println("\n=== Дубликаты ===")

        val duplicates = result.filter { it.value.size > 1 }

        if (duplicates.isEmpty()) {
            println("Дубликаты не найдены")
        } else {
            duplicates.forEach { (hash, files) ->
                println("\nХэш: $hash")
                files.forEach { println(it) }
            }
        }
    }
}

suspend fun findDuplicates(path: String): Map<String, List<String>> = coroutineScope {

    val files = File(path)
        .walkTopDown()
        .filter { it.isFile && it.extension == "json" }
        .toList()

    println("Найдено файлов: ${files.size}")

    val deferredResults = files.map { file ->
        async(Dispatchers.IO) {
            try {
                val hash = computeSHA256(file)
                hash to file.absolutePath
            } catch (e: Exception) {
                println("Ошибка при обработке файла ${file.name}: ${e.message}")
                null
            }
        }
    }

    val results = deferredResults.mapNotNull { it.await() }

    // Группировка по хэшу
    results.groupBy(
        keySelector = { it.first },
        valueTransform = { it.second }
    )
}

// suspend функция для вычисления SHA-256
suspend fun computeSHA256(file: File): String = withContext(Dispatchers.IO) {

    val digest = MessageDigest.getInstance("SHA-256")
    val bytes = file.readBytes()
    val hashBytes = digest.digest(bytes)

    hashBytes.joinToString("") { "%02x".format(it) }
}