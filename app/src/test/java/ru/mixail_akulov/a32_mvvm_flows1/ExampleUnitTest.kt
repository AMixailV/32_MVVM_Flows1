package ru.mixail_akulov.a32_mvvm_flows1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun testFlows() = runBlocking {
        val numbers = 1..10
        val flow: Flow<Int> = numbers.asFlow()

        println("Напечатать только если числа кратны 10: ")
        flow
            .filter { it % 2 == 0 }
            .map { it * 10 }
            .collect {
                println(it)
            }

        println("Напечатать только нечетные числа: ")
        flow
            .filter { it % 2 == 1 }
            .collect {
                println(it)
            }
    }
}