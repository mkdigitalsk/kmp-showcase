package sk.mkdigital.kmpshowcase.server.feature.note.persistence

import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object NotesTable : LongIdTable("notes") {
    /**
     * CASCADE makes erasure complete — the account's notes go in the same statement, so no route leaves rows behind.
     * [Regulation (EU) 2016/679, Art. 17](https://eur-lex.europa.eu/legal-content/EN/TXT/HTML/?uri=CELEX:32016R0679#art_17)
     */
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE).index()
    val title = varchar("title", TITLE_LENGTH)
    val content = text("content")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    /**
     * The entity tag is derived from this, never from a timestamp — now() is the transaction-start clock, so a slow
     * writer commits a tag older than one already published, and a stale check passes.
     */
    val version = long("version").default(0)

    const val TITLE_LENGTH = 200
    const val CONTENT_LENGTH = 10_000
}
