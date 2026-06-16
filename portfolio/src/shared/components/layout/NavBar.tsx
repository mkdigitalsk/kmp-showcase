'use client'

import Box from '@mui/material/Box'
import Link from 'next/link'
import { TextH6Bold } from '../text'
import { ThemeModeToggle } from '../ThemeModeToggle'

export function NavBar() {
  return (
    <Box
      component="header"
      sx={{
        position: 'sticky',
        top: 0,
        zIndex: (theme) => theme.zIndex.appBar,
        bgcolor: 'background.default',
        borderBottom: '1px solid',
        borderColor: 'divider',
      }}
    >
      <Box
        sx={{
          maxWidth: 880,
          mx: 'auto',
          px: 3,
          py: 1.5,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}
      >
        <Link href="/" style={{ textDecoration: 'none', color: 'inherit' }}>
          <TextH6Bold>Miroslav Kušnír</TextH6Bold>
        </Link>
        <ThemeModeToggle />
      </Box>
    </Box>
  )
}
