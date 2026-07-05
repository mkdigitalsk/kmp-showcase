package com.mk.kmpshowcase.presentation.base

import com.mk.kmpshowcase.data.analytics.AnalyticsClient
import com.mk.kmpshowcase.domain.useCase.analytics.TrackScreenUseCase
import com.mk.kmpshowcase.fake.NoOpAnalyticsClient
import com.mk.kmpshowcase.fake.NoOpLogger
import com.mk.kmpshowcase.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest {

    protected val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUpBase() {
        Dispatchers.setMain(testDispatcher)
        startKoin {
            modules(
                module {
                    single<AnalyticsClient> { NoOpAnalyticsClient }
                    single<Logger> { NoOpLogger }
                    single { TrackScreenUseCase(get()) }
                }
            )
        }
    }

    @AfterTest
    fun tearDownBase() {
        Dispatchers.resetMain()
        stopKoin()
    }
}
