package com.cartrack.challenge


import com.cartrack.challenge.api.common.scheduler.SchedulerProvider

import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler

/**
 * A [SchedulerProvider] for testing backed by a [TestScheduler] instance.
 */

open class TestSchedulerProvider(private val testScheduler: TestScheduler) : SchedulerProvider {

    override fun ui(): Scheduler {
        return testScheduler
    }

    override fun computation(): Scheduler {
        return testScheduler
    }

    override fun io(): Scheduler {
        return testScheduler
    }
}
