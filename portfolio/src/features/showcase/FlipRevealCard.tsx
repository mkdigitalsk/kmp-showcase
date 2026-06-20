'use client'

import Box from '@mui/material/Box'
import { motion, useReducedMotion } from 'motion/react'
import { useEffect, useRef, type KeyboardEvent, type ReactNode } from 'react'
import { TextCaptionNeutral60, TextH6Bold } from '@/shared/components'
import { CARD_FLIP_CLOSE_S, CARD_FLIP_S, FLIP_EASE, ICON_FLIP_S } from './flipTiming'
import type { ShowcaseApp } from './apps'

const CARD_RADIUS = 12
const PLAY_DELAY_MS = (ICON_FLIP_S + 0.18) * 1000

const faceStyle = {
  position: 'absolute',
  inset: 0,
  backfaceVisibility: 'hidden',
  WebkitBackfaceVisibility: 'hidden',
  borderRadius: `${CARD_RADIUS}px`,
  overflow: 'hidden',
} as const

interface FlipRevealCardProps {
  app: ShowcaseApp
  hint: string
  ariaLabel: string
  onActivate: () => void
  // The animated icon shown on the front face — it flips first, e.g. BankFlip / FoodFlip.
  frontIcon: ReactNode
  // 1 flips the card bottom-to-top, -1 top-to-bottom.
  flipSign?: 1 | -1
  height?: number
}

export function FlipRevealCard({
  app,
  hint,
  ariaLabel,
  onActivate,
  frontIcon,
  flipSign = -1,
  height = 240,
}: FlipRevealCardProps) {
  const { accent, label, previewSrc } = app
  const reduceMotion = useReducedMotion()
  const videoRef = useRef<HTMLVideoElement>(null)
  const playTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const flipTo = 180 * flipSign

  const clearPlayTimer = () => {
    if (playTimerRef.current) {
      clearTimeout(playTimerRef.current)
      playTimerRef.current = null
    }
  }
  const schedulePlay = () => {
    if (!previewSrc) return
    clearPlayTimer()
    playTimerRef.current = setTimeout(() => {
      videoRef.current?.play().catch(() => undefined)
    }, PLAY_DELAY_MS)
  }
  const stopPreview = () => {
    clearPlayTimer()
    const video = videoRef.current
    if (video) {
      video.pause()
      video.currentTime = 0
    }
  }
  const handleKeyDown = (event: KeyboardEvent) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      onActivate()
    }
  }

  useEffect(() => clearPlayTimer, [])

  return (
    <Box
      role="button"
      tabIndex={0}
      aria-label={ariaLabel}
      onClick={onActivate}
      onKeyDown={handleKeyDown}
      onMouseEnter={schedulePlay}
      onMouseLeave={stopPreview}
      sx={{
        height,
        cursor: 'pointer',
        borderRadius: `${CARD_RADIUS}px`,
        outline: 'none',
        perspective: 1200,
        transition: 'box-shadow 0.2s ease',
        '&:hover': { boxShadow: `0 0 0 2px ${accent}` },
        '&:focus-visible': { boxShadow: `0 0 0 2px ${accent}` },
      }}
    >
      <motion.div
        initial="rest"
        animate="rest"
        whileHover={reduceMotion ? undefined : 'active'}
        variants={{
          // Close: the card flips back first (no delay, a touch quicker), then the icon flips.
          rest: { rotateX: 0, transition: { duration: CARD_FLIP_CLOSE_S, delay: 0, ease: FLIP_EASE } },
          // Open: the icon flips first, then the card flips.
          active: { rotateX: flipTo, transition: { duration: CARD_FLIP_S, delay: ICON_FLIP_S, ease: FLIP_EASE } },
        }}
        style={{ position: 'relative', width: '100%', height: '100%', transformStyle: 'preserve-3d' }}
      >
        <Box sx={{ ...faceStyle, border: '1px solid', borderColor: 'divider', bgcolor: 'background.paper' }}>
          <Box sx={{ position: 'absolute', left: '50%', top: '38%', transform: 'translate(-50%, -50%)' }}>
            <Box
              sx={{
                width: 76,
                height: 76,
                borderRadius: '50%',
                display: 'grid',
                placeItems: 'center',
                bgcolor: `${accent}1A`,
              }}
            >
              {frontIcon}
            </Box>
          </Box>
          <Box
            sx={{
              position: 'absolute',
              left: 0,
              right: 0,
              top: '60%',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: 0.5,
            }}
          >
            <TextH6Bold>{label}</TextH6Bold>
            <TextCaptionNeutral60>{hint}</TextCaptionNeutral60>
          </Box>
        </Box>

        <Box sx={{ ...faceStyle, transform: `rotateX(${flipTo}deg)`, bgcolor: 'background.paper' }}>
          {previewSrc && (
            <Box
              component="video"
              ref={videoRef}
              src={previewSrc}
              muted
              loop
              playsInline
              preload="auto"
              aria-hidden
              sx={{ display: 'block', width: '100%', height: '100%', objectFit: 'cover' }}
            />
          )}
        </Box>
      </motion.div>
    </Box>
  )
}
