'use client'

import Box from '@mui/material/Box'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import { useTranslations } from 'next-intl'
import { useEffect, useState } from 'react'
import { PreviewScreen, type PreviewProps } from './PreviewKit'

// Food-delivery micro-theme: Uber Eats / Wolt vibe — clean & minimal, light type weights,
// generous whitespace, medium-large radius, emoji food thumbs. Accent only on price / CTA /
// tracking bar. Auto-simulates a tap on the 2nd dish row -> navigates to the order/tracking
// screen and loops (a "recorded interaction", not real clicks).

const R = 13 // medium-large, food-app radius

const DISHES = [
  { key: 'pizza', emoji: '🍕', meta: '★ 4.8 · 25 min', price: '€9.50' },
  { key: 'burger', emoji: '🍔', meta: '★ 4.6 · 20 min', price: '€7.90' },
  { key: 'ramen', emoji: '🍜', meta: '★ 4.9 · 30 min', price: '€11.00' },
]

const CHOSEN = DISHES[1]

export function FoodFlow({ accent }: PreviewProps) {
  const t = useTranslations('previews.food')
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
              <Box
                sx={{
                  display: 'flex',
                  alignItems: 'center',
                  px: 1.25,
                  py: 0.9,
                  mb: 1.25,
                  borderRadius: `${R}px`,
                  bgcolor: 'action.hover',
                  fontSize: 11,
                  fontWeight: 400,
                  color: 'text.secondary',
                }}
              >
                🔍&nbsp;{t('restaurantsNearYou')}
              </Box>
              {DISHES.map((d, i) => (
                <Box
                  key={d.key}
                  sx={{
                    position: 'relative',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 1.25,
                    p: 1,
                    mb: 0.75,
                    borderRadius: `${R}px`,
                    bgcolor: i === 1 && tapping ? 'action.selected' : 'background.paper',
                  }}
                >
                  <Box
                    sx={{
                      width: 38,
                      height: 38,
                      borderRadius: `${R - 3}px`,
                      display: 'grid',
                      placeItems: 'center',
                      fontSize: 20,
                      flexShrink: 0,
                      bgcolor: 'action.hover',
                    }}
                  >
                    {d.emoji}
                  </Box>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Box sx={{ fontSize: 12, fontWeight: 500, color: 'text.primary' }}>{t(`dish.${d.key}`)}</Box>
                    <Box sx={{ fontSize: 10, fontWeight: 400, color: 'text.secondary' }}>{d.meta}</Box>
                  </Box>
                  <Box sx={{ fontSize: 12, fontWeight: 600, color: accent }}>{d.price}</Box>
                  {tapping && i === 1 && !reduceMotion && (
                    <motion.div
                      initial={{ scale: 0, opacity: 0.5 }}
                      animate={{ scale: 2.4, opacity: 0 }}
                      transition={{ duration: 0.45 }}
                      style={{
                        position: 'absolute',
                        left: 26,
                        top: '50%',
                        width: 24,
                        height: 24,
                        marginTop: -12,
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
              key="order"
              initial={{ opacity: 0, x: 10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '0 12px' }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.25, mb: 1.25 }}>
                <Box
                  sx={{
                    width: 46,
                    height: 46,
                    borderRadius: `${R}px`,
                    display: 'grid',
                    placeItems: 'center',
                    fontSize: 26,
                    flexShrink: 0,
                    bgcolor: 'action.hover',
                  }}
                >
                  {CHOSEN.emoji}
                </Box>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                  <Box sx={{ fontSize: 13, fontWeight: 500, color: 'text.primary' }}>{t(`dish.${CHOSEN.key}`)}</Box>
                  <Box sx={{ fontSize: 11, color: 'text.secondary' }}>{CHOSEN.meta}</Box>
                </Box>
                <Box sx={{ fontSize: 13, fontWeight: 600, color: accent }}>{CHOSEN.price}</Box>
              </Box>

              <Box
                sx={{
                  borderRadius: `${R}px`,
                  p: 1.25,
                  bgcolor: `${accent}14`,
                }}
              >
                <Box sx={{ fontSize: 11.5, fontWeight: 600, color: 'text.primary', mb: 0.9 }}>
                  {t('onTheWay')} · 12 min
                </Box>
                <Box sx={{ height: 5, borderRadius: 3, bgcolor: 'action.hover', overflow: 'hidden', mb: 1.1 }}>
                  <motion.div
                    initial={{ width: reduceMotion ? '64%' : '12%' }}
                    animate={{ width: reduceMotion ? '64%' : '64%' }}
                    transition={reduceMotion ? undefined : { duration: 2.2, ease: 'easeInOut' }}
                    style={{ height: '100%', borderRadius: 3, background: accent }}
                  />
                </Box>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.75 }}>
                  <Box sx={{ fontSize: 16, lineHeight: 1 }}>🛵</Box>
                  <Box sx={{ flex: 1, fontSize: 11, fontWeight: 500, color: 'text.primary' }}>{t('driver')}</Box>
                  <Box sx={{ fontSize: 10, color: 'text.secondary' }}>{t('kmAway', { distance: '0.8' })}</Box>
                </Box>
              </Box>
            </motion.div>
          )}
        </AnimatePresence>
      </Box>
    </PreviewScreen>
  )
}
