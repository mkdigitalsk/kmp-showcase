package com.mk.kmpshowcase.server.feature.user.service

internal enum class Role {
    ADMIN, CLIENT;

    // "You only see users up to your own role": ADMIN sees everyone; CLIENT sees only peers, never admins.
    val visibleRoles: Set<Role>
        get() = when (this) {
            ADMIN -> entries.toSet()
            CLIENT -> setOf(CLIENT)
        }
}
