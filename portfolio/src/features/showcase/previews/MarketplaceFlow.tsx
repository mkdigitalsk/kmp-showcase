'use client'

import Box from '@mui/material/Box'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import { useTranslations } from 'next-intl'
import { useEffect, useState } from 'react'
import { PreviewScreen, type PreviewProps } from './PreviewKit'

// Marketplace micro-theme: Vinted/Etsy vibe — friendly, community, slightly denser,
// medium radius, emoji item thumbs. Trust elements (seller ★, 🔒 escrow, 💬 chat) and
// price are highlighted in the accent. Auto-simulates a tap on the first listing ->
// navigates to the listing detail and loops (a "recorded interaction", not real clicks).

const R = 10 // medium, friendly radius

const PILL_KEYS = ['pillAll', 'pillTech', 'pillHome'] as const
const LISTINGS = [
  { key: 'camera', emoji: '📷', price: '€240', rating: '4.9' },
  { key: 'bike', emoji: '🚲', price: '€180', rating: '4.7' },
  { key: 'designerChair', emoji: '🪑', price: '€60', rating: '4.8' },
] as const
const FEATURED = LISTINGS[0]

export function MarketplaceFlow({ accent }: PreviewProps) {
  const t = useTranslations('previews.marketplace')
  const reduceMotion = useReducedMotion()
  const [screen, setScreen] = useState(0)
  const [tapping, setTapping] = useState(false)

  useEffect(() => {
    if (reduceMotion) return undefined
    let flip: ReturnType<typeof setTimeout>
    const loop = setInterval(() => {
      setTapping(true)
      flip = setTimeout(() => {
        setScreen((s) => 1 - s)
        setTapping(false)
      }, 400)
    }, 2000)
    return () => {
      clearInterval(loop)
      clearTimeout(flip)
    }
  }, [reduceMotion])

  return (
    <PreviewScreen>
      <Box sx={{ position: 'relative', flex: 1, overflow: 'hidden' }}>
        <AnimatePresence mode="wait">
          {screen === 0 ? (
            <motion.div
              key="browse"
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '0 12px' }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.75, mb: 1 }}>
                {PILL_KEYS.map((pillKey, i) => (
                  <Box
                    key={pillKey}
                    sx={{
                      fontSize: 10,
                      fontWeight: 700,
                      px: 1,
                      py: 0.4,
                      borderRadius: `${R}px`,
                      bgcolor: i === 0 ? accent : 'action.hover',
                      color: i === 0 ? 'common.white' : 'text.secondary',
                    }}
                  >
                    {t(pillKey)}
                  </Box>
                ))}
                <Box
                  sx={{
                    ml: 'auto',
                    width: 24,
                    height: 24,
                    borderRadius: `${R}px`,
                    display: 'grid',
                    placeItems: 'center',
                    fontSize: 12,
                    bgcolor: 'action.hover',
                  }}
                >
                  🔍
                </Box>
              </Box>
              {LISTINGS.map((l, i) => (
                <Box
                  key={l.key}
                  sx={{
                    position: 'relative',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 1,
                    p: 0.75,
                    mb: 0.75,
                    borderRadius: `${R}px`,
                    bgcolor: 'action.hover',
                  }}
                >
                  <Box
                    sx={{
                      width: 32,
                      height: 32,
                      borderRadius: `${R - 2}px`,
                      display: 'grid',
                      placeItems: 'center',
                      fontSize: 18,
                      bgcolor: 'background.paper',
                    }}
                  >
                    {l.emoji}
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Box sx={{ fontSize: 11.5, fontWeight: 700, color: 'text.primary' }}>{t(l.key)}</Box>
                    <Box sx={{ fontSize: 9.5, fontWeight: 700, color: accent }}>
                      ★{l.rating} {t('seller')}
                    </Box>
                  </Box>
                  <Box sx={{ fontSize: 12, fontWeight: 800, color: accent }}>{l.price}</Box>
                  {tapping && i === 0 && !reduceMotion && (
                    <motion.div
                      initial={{ scale: 0, opacity: 0.5 }}
                      animate={{ scale: 2.4, opacity: 0 }}
                      transition={{ duration: 0.45 }}
                      style={{
                        position: 'absolute',
                        left: 16,
                        top: '50%',
                        width: 22,
                        height: 22,
                        marginTop: -11,
                        borderRadius: '50%',
                        background: accent,
                      }}
                    />
                  )}
                </Box>
              ))}
            </motion.div>
          ) : (
            <motion.div
              key="listing"
              initial={{ opacity: 0, x: 10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '0 12px' }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.25, mb: 1 }}>
                <Box
                  sx={{
                    width: 48,
                    height: 48,
                    borderRadius: `${R}px`,
                    display: 'grid',
                    placeItems: 'center',
                    fontSize: 28,
                    bgcolor: 'action.hover',
                  }}
                >
                  {FEATURED.emoji}
                </Box>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                  <Box sx={{ fontSize: 13, fontWeight: 800, color: 'text.primary' }}>{t(FEATURED.key)}</Box>
                  <Box sx={{ fontSize: 15, fontWeight: 800, color: accent }}>{FEATURED.price}</Box>
                </Box>
              </Box>
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 1,
                  py: 0.5,
                  mb: 0.75,
                }}
              >
                <Box sx={{ fontSize: 11, fontWeight: 700, color: accent }}>★{FEATURED.rating}</Box>
                <Box sx={{ fontSize: 10.5, color: 'text.secondary' }}>· 230 {t('sales')}</Box>
              </Box>
              <Box
                sx={{
                  fontSize: 10.5,
                  fontWeight: 700,
                  color: accent,
                  px: 1,
                  py: 0.75,
                  mb: 1,
                  borderRadius: `${R}px`,
                  bgcolor: `${accent}1A`,
                }}
              >
                🔒 {t('escrow')}
              </Box>
              <Box
                sx={{
                  fontSize: 12,
                  fontWeight: 800,
                  color: 'common.white',
                  textAlign: 'center',
                  py: 1,
                  borderRadius: `${R}px`,
                  bgcolor: accent,
                }}
              >
                💬 {t('messageSeller')}
              </Box>
            </motion.div>
          )}
        </AnimatePresence>
      </Box>
    </PreviewScreen>
  )
}
