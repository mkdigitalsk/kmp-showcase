package com.mk.kmpshowcase;

enum class BuildType {
    DEBUG, RELEASE;

    val isDebug: Boolean get() = this == DEBUG
    val isRelease: Boolean get() = this == RELEASE

    companion object {
        fun from(name: String): BuildType = if (name == "release") RELEASE else DEBUG
    }
}
