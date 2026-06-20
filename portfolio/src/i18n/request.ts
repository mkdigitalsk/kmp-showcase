import { getRequestConfig } from 'next-intl/server'
import { cookies, headers } from 'next/headers'
import { DEFAULT_LOCALE, LOCALES } from '../shared/i18n/locales'

const SUPPORTED = LOCALES.map((l) => l.code as string)

// Pick the closest supported locale from the browser's Accept-Language header
// (its configured language), matching by language (e.g. "sk" -> "sk-SK").
// Falls back to the default locale.
function matchBrowserLocale(acceptLanguage: string | null): string {
  if (!acceptLanguage) return DEFAULT_LOCALE as string

  const requested = acceptLanguage
    .split(',')
    .map((part) => {
      const [tag, q] = part.trim().split(';q=')
      return { tag: tag.toLowerCase(), q: q ? Number.parseFloat(q) : 1 }
    })
    .sort((a, b) => b.q - a.q)

  for (const { tag } of requested) {
    const language = tag.split('-')[0]
    const match = SUPPORTED.find(
      (code) => code.toLowerCase() === tag || code.toLowerCase().split('-')[0] === language,
    )
    if (match) return match
  }

  return DEFAULT_LOCALE as string
}

export default getRequestConfig(async () => {
  // A user's explicit choice (cookie) wins; otherwise follow the browser default.
  const cookieLocale = (await cookies()).get('locale')?.value

  const locale =
    cookieLocale && SUPPORTED.includes(cookieLocale)
      ? cookieLocale
      : matchBrowserLocale((await headers()).get('accept-language'))

  return {
    locale,
    messages: (await import(`../../messages/${locale}.json`)).default,
  }
})
