package com.mk.kmpshowcase.contracts

/**
 * Single source of truth for the API URL version — shared by the server (route paths) and
 * every consumer (mobile HttpClient base, web). Bumping [CURRENT] migrates server AND clients
 * at once, so no path can drift. The version is part of the API contract, hence it lives here.
 *
 * Once a URL is public it is a contract: add new versions (V2, V3…) alongside the old one,
 * never change an existing path silently.
 */
object ApiVersion {
    const val V1 = "v1"
    const val CURRENT = V1
    const val BASE = "/" + CURRENT // api. subdomain already signals "API" — no redundant /api prefix
}
