package com.mk.kmpshowcase.server.core

// Mapped to 413 in StatusPages — thrown wherever a body-size cap is enforced (document upload).
internal class PayloadTooLargeException(message: String) : RuntimeException(message)
