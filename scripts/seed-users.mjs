// The showcase's demo accounts, created through the same sign-up endpoint a visitor uses. Going
// through the API rather than inserting rows is what makes the seed worth running: it exercises
// validation and password hashing, so a broken sign-up fails here instead of in front of someone.
//
// These credentials are public by design — the sign-in screen prefills test01 so the showcase opens
// without an account.
//
//   node scripts/seed-users.mjs --env=production
import { target } from './db.mjs'

const PASSWORD = 'MKDigitalTest1@'
const EMAILS = [1, 2, 3].map((n) => `test0${n}@mkdigital.sk`)

const { name, api } = target()

// 409 is the seed having already run. Anything else is a real failure and has to end the run non-zero,
// or a half-seeded environment reports success.
async function signUp(email) {
  const response = await fetch(`${api}/v1/auth/sign-up`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password: PASSWORD }),
  })
  if (response.status === 201) return 'created'
  if (response.status === 409) return 'already there'
  throw new Error(`${email}: ${response.status} ${await response.text()}`)
}

console.log(`Seeding ${EMAILS.length} demo accounts into ${name} (${api})`)

let failed = false
for (const email of EMAILS) {
  try {
    console.log(`  ${email} — ${await signUp(email)}`)
  } catch (error) {
    console.error(`  ${email} — FAILED: ${error.message}`)
    failed = true
  }
}

process.exit(failed ? 1 : 0)
