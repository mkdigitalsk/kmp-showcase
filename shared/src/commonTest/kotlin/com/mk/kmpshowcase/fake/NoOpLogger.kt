package com.mk.kmpshowcase.fake

import com.mk.kmpshowcase.util.Logger

object NoOpLogger : Logger {
    override fun e(log: String) = Unit
    override fun e(e: Throwable) = Unit
    override fun e(log: String, e: Throwable) = Unit
    override fun d(log: String) = Unit
}
