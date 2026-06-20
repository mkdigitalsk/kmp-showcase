'use client'

import Box from '@mui/material/Box'
import { motion, useReducedMotion } from 'motion/react'
import { useEffect, useRef, type KeyboardEvent } from 'react'
import { TextCaptionNeutral60, TextH6Bold } from '@/shared/components'
import { HeartBeat } from './HeartBeat'
import { fasterClose } from './closeTransition'
import { iconAnimations } from './iconAnimations'
import { revealAnimations } from './revealAnimations'
import type { ShowcaseApp } from './apps'

const CARD_RADIUS = 12

const REVEAL_DURATION_MS = 550
// Start playback this long BEFORE the reveal finishes, so the video is already
// in motion by the moment it becomes visible.
const PLAY_OFFSET_MS = 130

interface AppRevealCardProps {
  app: ShowcaseApp
  hint: string
  ariaLabel: string
  onActivate: () => void
  height?: number
}

export function AppRevealCard({ app, hint, ariaLabel, onActivate, height = 240 }: AppRevealCardProps) {
  const { Icon, label, accent, previewSrc, iconAnimation } = app
  const reduceMotion = useReducedMotion()
  const videoRef = useRef<HTMLVideoElement>(null)
  const playTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const iconAnim = iconAnimations[iconAnimation]
  const reveal = revealAnimations[iconAnimation]

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
    }, REVEAL_DURATION_MS - PLAY_OFFSET_MS)
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
        transition: 'box-shadow 0.2s ease',
        '&:hover': { boxShadow: `0 0 0 2px ${accent}` },
        '&:focus-visible': { boxShadow: `0 0 0 2px ${accent}` },
      }}
    >
      <motion.div
        initial="rest"
        animate="rest"
        whileHover={reduceMotion ? undefined : 'active'}
        style={{ position: 'relative', width: '100%', height: '100%', borderRadius: CARD_RADIUS, overflow: 'hidden' }}
      >
        <Box
          sx={{
            position: 'absolute',
            inset: 0,
            borderRadius: `${CARD_RADIUS}px`,
            border: '1px solid',
            borderColor: 'divider',
            bgcolor: 'background.paper',
          }}
        />

        {previewSrc && (
          <motion.div
            variants={{
              rest: { ...reveal.rest, transition: fasterClose(reveal.transition) },
              active: { ...reveal.active, transition: reveal.transition },
            }}
            style={{ position: 'absolute', inset: 0 }}
          >
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
          </motion.div>
        )}

        <Box
          sx={{
            position: 'absolute',
            left: '50%',
            top: '38%',
            transform: 'translate(-50%, -50%)',
            pointerEvents: 'none',
          }}
        >
          {iconAnimation === 'heart' ? (
            <HeartBeat accent={accent} />
          ) : (
            <motion.div
              variants={{
                rest: { ...iconAnim.variants.rest, transition: fasterClose(iconAnim.transition) },
                active: { ...iconAnim.variants.active, transition: iconAnim.transition },
              }}
              style={{
                width: 76,
                height: 76,
                borderRadius: '50%',
                display: 'grid',
                placeItems: 'center',
                color: accent,
                backgroundColor: `${accent}1A`,
              }}
            >
              <Icon sx={{ fontSize: 38 }} />
            </motion.div>
          )}
        </Box>

        <motion.div
          variants={{ rest: { opacity: 1 }, active: { opacity: 0 } }}
          transition={{ duration: 0.25 }}
          style={{
            position: 'absolute',
            left: 0,
            right: 0,
            top: '60%',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            gap: 4,
            pointerEvents: 'none',
          }}
        >
          <TextH6Bold>{label}</TextH6Bold>
          <TextCaptionNeutral60>{hint}</TextCaptionNeutral60>
        </motion.div>
      </motion.div>
    </Box>
  )
}
