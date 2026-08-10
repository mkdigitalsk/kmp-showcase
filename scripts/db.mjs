// One environment name selects both the Neon branch and the API in front of it — a pair that has to
// agree is one value, not two. It is required rather than defaulted, so no run inherits a leftover
// target.
//
//   node <script>.mjs --env=production
import { execFileSync } from 'node:child_process'
import { neon } from '@neondatabase/serverless'

// Not secret, but it identifies infrastructure and differs per deploy, so it is passed in rather than
// written down. `neon projects list` prints both.
const PROJECT = process.env.NEON_PROJECT_ID
const ORG = process.env.NEON_ORG_ID

const ENVIRONMENTS = {
  local: { branch: null, api: 'http://localhost:8080' },
  production: { branch: 'production', api: 'https://api.showcase.mkdigital.sk' },
}

function select(argv) {
  const name = argv.find((a) => a.startsWith('--env='))?.slice('--env='.length)
  const env = name && ENVIRONMENTS[name]
  if (!env) {
    console.error(`Which environment? Pass --env=${Object.keys(ENVIRONMENTS).join(' | --env=')}.`)
    process.exit(2)
  }
  return { name, ...env }
}

/** The API in front of an environment. A seed goes through it, so it needs no Neon branch. */
export function target(argv = process.argv) {
  return select(argv)
}

/** A database connection and the API that sits in front of the same environment. */
export function connect(argv = process.argv) {
  const { name, branch, api } = select(argv)
  if (!branch) {
    console.error(`--env=${name} has no Neon branch; it is the locally-run server.`)
    process.exit(2)
  }
  if (!PROJECT || !ORG) {
    console.error('Set NEON_PROJECT_ID and NEON_ORG_ID. `neon projects list` prints both.')
    process.exit(2)
  }

  const url = execFileSync('neon', ['connection-string', branch, '--project-id', PROJECT, '--org-id', ORG], {
    encoding: 'utf8',
  }).trim()

  return { sql: neon(url), env: name, branch, api, host: new URL(url).host }
}
