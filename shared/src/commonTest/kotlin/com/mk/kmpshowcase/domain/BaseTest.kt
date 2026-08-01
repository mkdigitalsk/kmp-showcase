package com.mk.kmpshowcase.domain

import kotlin.test.BeforeTest

abstract class BaseTest<ClassUnderTest> {
    abstract var classUnderTest: ClassUnderTest

    @BeforeTest
    fun setup() {
        beforeEach()
    }

    abstract fun beforeEach()
}
