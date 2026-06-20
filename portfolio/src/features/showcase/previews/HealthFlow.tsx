'use client'

import { Bolt, DirectionsRun, LocalFireDepartment, Timer } from '@mui/icons-material'
import Box from '@mui/material/Box'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import { useTranslations } from 'next-intl'
import { useEffect, useState } from 'react'
import { PreviewScreen, type PreviewProps } from './PreviewKit'

// Health & Fitness micro-theme: Strava/Nike Training Club vibe — bold (800) weights,
// big accent numbers, energetic high contrast, real MUI icons. Auto-simulates a tap on
// the "Start workout" CTA -> navigates to the active workout screen and loops (a
// "recorded interaction", not real clicks).

const R = 12 // medium, sporty radius

const STATS = [
  { Icon: DirectionsRun, value: '8,240', key: 'steps' },
  { Icon: LocalFireDepartment, value: '540', key: 'kcal' },
  { Icon: Timer, value: '47', key: 'min' },
]

const METRICS = ['3.2 km', '320 kcal', "5'10/km"]

const RING_SIZE = 110
const RING_STROKE = 9
const RING_R = (RING_SIZE - RING_STROKE) / 2
const RING_C = 2 * Math.PI * RING_R

export function HealthFlow({ accent }: PreviewProps) {
  const t = useTranslations('previews.health')
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
              key="today"
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '0 12px' }}
            >
              <Box sx={{ fontSize: 22, fontWeight: 800, color: 'text.primary', letterSpacing: '-0.02em' }}>
                {t('today')}
              </Box>
              <Box sx={{ fontSize: 10.5, fontWeight: 700, color: 'text.secondary', mb: 1.25 }}>
                Mon, Jun 20
              </Box>
              <Box sx={{ display: 'flex', gap: 0.75, mb: 1.5 }}>
                {STATS.map((s) => (
                  <Box
                    key={s.key}
                    sx={{
                      flex: 1,
                      borderRadius: `${R}px`,
                      bgcolor: 'action.hover',
                      p: 1,
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'flex-start',
                    }}
                  >
                    <s.Icon sx={{ fontSize: 14, color: accent, mb: 0.5 }} />
                    <Box
                      sx={{
                        fontSize: 18,
                        fontWeight: 800,
                        color: accent,
                        letterSpacing: '-0.02em',
                        fontVariantNumeric: 'tabular-nums',
                        lineHeight: 1.1,
                      }}
                    >
                      {s.value}
                    </Box>
                    <Box sx={{ fontSize: 9.5, fontWeight: 700, color: 'text.secondary' }}>{t(s.key)}</Box>
                  </Box>
                ))}
              </Box>
              <Box
                sx={{
                  position: 'relative',
                  height: 44,
                  borderRadius: `${R}px`,
                  bgcolor: accent,
                  color: 'common.white',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: 0.75,
                  overflow: 'hidden',
                }}
              >
                <Bolt sx={{ fontSize: 18 }} />
                <Box sx={{ fontSize: 13, fontWeight: 800, letterSpacing: '0.01em' }}>{t('startWorkout')}</Box>
                {tapping && !reduceMotion && (
                  <motion.div
                    initial={{ scale: 0, opacity: 0.5 }}
                    animate={{ scale: 6, opacity: 0 }}
                    transition={{ duration: 0.45 }}
                    style={{
                      position: 'absolute',
                      left: '50%',
                      top: '50%',
                      width: 28,
                      height: 28,
                      marginLeft: -14,
                      marginTop: -14,
                      borderRadius: '50%',
                      background: 'rgba(255,255,255,0.7)',
                    }}
                  />
                )}
              </Box>
            </motion.div>
          ) : (
            <motion.div
              key="workout"
              initial={{ opacity: 0, x: 10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '4px 12px 0', display: 'flex', flexDirection: 'column', alignItems: 'center' }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 1 }}>
                <DirectionsRun sx={{ fontSize: 16, color: accent }} />
                <Box sx={{ fontSize: 12, fontWeight: 800, color: 'text.primary', letterSpacing: '-0.01em' }}>
                  {t('morningRun')}
                </Box>
              </Box>
              <Box sx={{ position: 'relative', width: RING_SIZE, height: RING_SIZE, mb: 1.25 }}>
                <svg width={RING_SIZE} height={RING_SIZE} style={{ transform: 'rotate(-90deg)' }}>
                  <circle
                    cx={RING_SIZE / 2}
                    cy={RING_SIZE / 2}
                    r={RING_R}
                    fill="none"
                    stroke="rgba(128,128,128,0.18)"
                    strokeWidth={RING_STROKE}
                  />
                  <motion.circle
                    cx={RING_SIZE / 2}
                    cy={RING_SIZE / 2}
                    r={RING_R}
                    fill="none"
                    stroke={accent}
                    strokeWidth={RING_STROKE}
                    strokeLinecap="round"
                    strokeDasharray={RING_C}
                    initial={{ strokeDashoffset: reduceMotion ? RING_C * 0.25 : RING_C }}
                    animate={
                      reduceMotion
                        ? { strokeDashoffset: RING_C * 0.25 }
                        : { strokeDashoffset: [RING_C, RING_C * 0.2, RING_C * 0.2] }
                    }
                    transition={reduceMotion ? undefined : { duration: 2.6, repeat: Infinity, ease: 'easeInOut' }}
                  />
                </svg>
                <Box
                  sx={{
                    position: 'absolute',
                    inset: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                >
                  <Box
                    sx={{
                      fontSize: 24,
                      fontWeight: 800,
                      color: 'text.primary',
                      letterSpacing: '-0.02em',
                      fontVariantNumeric: 'tabular-nums',
                      lineHeight: 1,
                    }}
                  >
                    12:34
                  </Box>
                  <Box sx={{ fontSize: 9, fontWeight: 700, color: 'text.secondary', mt: 0.25 }}>{t('elapsed')}</Box>
                </Box>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                {METRICS.map((m, i) => (
                  <Box key={m} sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <Box
                      sx={{
                        fontSize: 11,
                        fontWeight: 800,
                        color: 'text.primary',
                        fontVariantNumeric: 'tabular-nums',
                      }}
                    >
                      {m}
                    </Box>
                    {i < METRICS.length - 1 && (
                      <Box sx={{ fontSize: 11, fontWeight: 700, color: 'text.disabled' }}>·</Box>
                    )}
                  </Box>
                ))}
              </Box>
            </motion.div>
          )}
        </AnimatePresence>
      </Box>
    </PreviewScreen>
  )
}
