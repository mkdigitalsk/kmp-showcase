package com.mk.kmpshowcase.data.local.database

import app.cash.sqldelight.db.SqlDriver

const val DATABASE_NAME = "app.db"

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
