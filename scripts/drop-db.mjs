// Destructive: drops every table in the public schema so the next server start rebuilds them.
//
// SchemaUtils.createMissingTablesAndColumns adds a missing column and never drops one, so a column
// removed from the code outlives it and breaks the next insert. This is how you start over.
//
// Enumerated rather than listed by name: a hardcoded list silently stops covering tables added later.
//
//   node scripts/drop-db.mjs --env=production --yes
import { connect } from './db.mjs'

const { sql, branch, host } = connect()

if (!process.argv.includes('--yes')) {
  console.error(`About to drop every table on ${branch} (${host}). Re-run with --yes.`)
  process.exit(2)
}

const tables = await sql`SELECT tablename FROM pg_tables WHERE schemaname = 'public'`
for (const { tablename } of tables) {
  await dropTable(tablename)
}

const left = await sql`SELECT tablename FROM pg_tables WHERE schemaname = 'public'`
console.log(`Dropped ${tables.length} table(s) on ${branch}. Remaining:`, left.map((r) => r.tablename))
console.log('Redeploy or restart the server to rebuild them.')

/**
 * A name read from pg_tables, never typed in: a template would bind it as a value, which DROP TABLE takes none of.
 */
function dropTable(name) {
  return sql.query(`DROP TABLE IF EXISTS "${name}" CASCADE`)
}
