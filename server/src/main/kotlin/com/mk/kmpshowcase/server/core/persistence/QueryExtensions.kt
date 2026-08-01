package com.mk.kmpshowcase.server.core.persistence

import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.core.ResultRow

internal fun <T> Query.mapToSingleOrNull(transform: (ResultRow) -> T): T? =
    map(transform).singleOrNull()
