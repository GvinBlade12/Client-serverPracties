package com.example.ex12

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FactsViewModel : ViewModel() {

    private val facts = listOf(
        "Осьминоги имеют три сердца.",
        "Слоны не умеют прыгать.",
        "У коал отпечатки пальцев как у людей.",
        "Жирафы спят всего 10–30 минут в сутки.",
        "У улитки около 25 000 зубов.",
        "Киты могут задерживать дыхание до 90 минут.",
        "Акулы существовали раньше деревьев.",
        "Пингвины делают предложения с камушком.",
        "Коровы имеют лучших друзей.",
        "Кошки могут издавать более 100 звуков.",
        "Собаки чувствуют время.",
        "Муравьи не спят.",
        "Летучие мыши всегда поворачивают налево, вылетая из пещеры.",
        "У дельфинов есть имена.",
        "Бабочки пробуют еду лапками."
    )

    fun getRandomFact(): Flow<String> = flow {
        val delayTime = Random.nextLong(1500, 3000)
        delay(delayTime)
        emit(facts.random())
    }
}