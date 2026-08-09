// One environment name selects everything: the Neon branch AND the API the scripts talk to.
// A pair that must agree is one value, not two.
//
// Nothing here reads .env. A credential for an environment this machine does not run has no reason to
// sit on disk, and a stored password is wrong the moment the role is rotated.
//
// The environment is required rather than defaulted, so no run inherits whichever one a file was left
// pointing at.
//
//   node <script>.mjs --env=production
import { execFileSync } from 'node:child_process'
import { neon } from '@neondatabase/serverless'

const PROJECT = 'wild-butterfly-73160403' // showcase — `neon projects list`
const ORG = 'org-fragrant-math-50654029' // MK Digital — the CLI prompts for it when not a terminal

// The showcase has one deployed environment. Preview builds read the same API, so there is no second
// branch to name here.
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

/** A database connection and the API that sits in front of the same environment. */
export function connect(argv = process.argv) {
  const { name, branch, api } = select(argv)
  if (!branch) {
    console.error(`--env=${name} has no Neon branch; it is the locally-run server.`)
    process.exit(2)
  }

  const url = execFileSync('neon', ['connection-string', branch, '--project-id', PROJECT, '--org-id', ORG], {
    encoding: 'utf8',
  }).trim()

  return { sql: neon(url), env: name, branch, api, host: new URL(url).host }
}
