package com.blacksmith.quranApp.data.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit

suspend fun tickerFlow(
    start: Long,
    totalPeriod: Long,
    initialDelay: Long,
    tickDelayPeriod: Long,
    timeUnit: TimeUnit
) = flow<TickFlowState<Long>> {
    //convert initial delay to millisecond using given timeUnit
    val initialDelayMs = timeUnit.toMillis(initialDelay)
    //delay with given initial value
    delay(initialDelayMs)

    var counter = start
    while (counter < totalPeriod) {
        emit(TickFlowState.Tick(counter))
        counter += tickDelayPeriod
        //convert period to millisecond using given timeUnit
        val tickDelayPeriodMs = timeUnit.toMillis(tickDelayPeriod)
        delay(tickDelayPeriodMs)
    }
    emit(TickFlowState.Finish(counter))
}

sealed class TickFlowState<out R> {
    data class Finish<out T>(val tick: T) : TickFlowState<T>()
    data class Tick<out T>(val tick: T) : TickFlowState<T>()
}