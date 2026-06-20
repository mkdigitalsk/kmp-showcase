'use client'

import Box from '@mui/material/Box'
import { useTranslations } from 'next-intl'
import { useState } from 'react'
import { Reveal, TextBody1Neutral60, TextH1Bold } from '@/shared/components'
import { AppDetailDialog } from './AppDetailDialog'
import { AppRevealCard } from './AppRevealCard'
import { BankFlip } from './BankFlip'
import { FlipRevealCard } from './FlipRevealCard'
import { FoodFlip } from './FoodFlip'
import { showcaseApps, type ShowcaseApp } from './apps'

const STAGGER_STEP = 0.06
const STAGGER_MAX = 0.3

export function AppShowcase() {
  const t = useTranslations()
  const [selectedApp, setSelectedApp] = useState<ShowcaseApp | null>(null)

  return (
    <Box
      component="main"
      sx={{ maxWidth: 1080, mx: 'auto', px: 3, pt: { xs: 6, md: 10 }, pb: { xs: 8, md: 12 } }}
    >
      <Reveal>
        <Box sx={{ mb: 2 }}>
          <TextH1Bold align="center">{t('home.headline')}</TextH1Bold>
        </Box>
      </Reveal>
      <Reveal delay={0.08}>
        <Box sx={{ mb: { xs: 5, md: 7 }, maxWidth: 620, mx: 'auto' }}>
          <TextBody1Neutral60 align="center">{t('home.subline')}</TextBody1Neutral60>
        </Box>
      </Reveal>

      <Box
        sx={{
          display: 'grid',
          gap: { xs: 2, md: 3 },
          gridTemplateColumns: { xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr' },
        }}
      >
        {showcaseApps.map((app, index) => (
          <Reveal key={app.id} delay={Math.min(index * STAGGER_STEP, STAGGER_MAX)}>
            {app.iconAnimation === 'flip' ? (
              <FlipRevealCard
                app={app}
                hint={t('home.cardHint')}
                ariaLabel={t('home.openDetail', { name: app.label })}
                onActivate={() => setSelectedApp(app)}
                frontIcon={<BankFlip accent={app.accent} flipSign={-1} />}
                flipSign={-1}
              />
            ) : app.iconAnimation === 'food' ? (
              <FlipRevealCard
                app={app}
                hint={t('home.cardHint')}
                ariaLabel={t('home.openDetail', { name: app.label })}
                onActivate={() => setSelectedApp(app)}
                frontIcon={<FoodFlip accent={app.accent} flipSign={1} />}
                flipSign={1}
              />
            ) : (
              <AppRevealCard
                app={app}
                hint={t('home.cardHint')}
                ariaLabel={t('home.openDetail', { name: app.label })}
                onActivate={() => setSelectedApp(app)}
              />
            )}
          </Reveal>
        ))}
      </Box>

      <AppDetailDialog app={selectedApp} open={selectedApp !== null} onClose={() => setSelectedApp(null)} />
    </Box>
  )
}
