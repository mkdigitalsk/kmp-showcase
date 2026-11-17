package sk.mkdigital.kmpshowcase.server.core

/** Mapped to 413 in StatusPages. */
internal class PayloadTooLargeException(message: String) : RuntimeException(message)
