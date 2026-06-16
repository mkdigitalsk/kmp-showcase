import type { Metadata } from 'next'
import '@fontsource/roboto/300.css'
import '@fontsource/roboto/400.css'
import '@fontsource/roboto/500.css'
import '@fontsource/roboto/700.css'
import { AppRouterCacheProvider } from '@mui/material-nextjs/v16-appRouter'
import { ThemeProvider } from '@mui/material/styles'
import Box from '@mui/material/Box'
import CssBaseline from '@mui/material/CssBaseline'
import InitColorSchemeScript from '@mui/material/InitColorSchemeScript'
import { Footer, NavBar } from '@/shared/components'
import { theme } from '@/shared/theme'
import './globals.css'

export const metadata: Metadata = {
  title: 'Miroslav Kušnír — Cross-platform engineer (mobile · web · backend)',
  description:
    'I build cross-platform apps end to end — iOS, Android, web, and backend — with clean architecture and production-grade patterns.',
}

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body>
        <InitColorSchemeScript defaultMode="system" />
        <AppRouterCacheProvider options={{ enableCssLayer: true }}>
          <ThemeProvider theme={theme} defaultMode="system">
            <CssBaseline />
            <Box sx={{ display: 'flex', flexDirection: 'column', minHeight: '100dvh' }}>
              <NavBar />
              <Box sx={{ flex: 1 }}>{children}</Box>
              <Footer />
            </Box>
          </ThemeProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  )
}
