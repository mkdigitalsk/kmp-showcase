'use client'

import { Favorite } from '@mui/icons-material'
import { motion } from 'motion/react'
import { fasterClose } from './closeTransition'

// pump 1 (small), pump 2 grows across the whole card, then fades to reveal the video.
const SEQUENCE = { duration: 1, ease: 'easeInOut' as const, times: [0, 0.2, 0.4, 0.75, 1] }
const CLOSE = fasterClose(SEQUENCE)

interface HeartBeatProps {
  accent: string
}

export function HeartBeat({ accent }: HeartBeatProps) {
  return (
    <div style={{ position: 'relative', width: 76, height: 76, display: 'grid', placeItems: 'center' }}>
      <motion.div
        variants={{
          rest: { opacity: 1, transition: CLOSE },
          active: { opacity: [1, 1, 1, 0, 0], transition: SEQUENCE },
        }}
        style={{ position: 'absolute', inset: 0, borderRadius: '50%', backgroundColor: `${accent}1A` }}
      />
      <motion.div
        variants={{
          rest: { scale: 1, opacity: 1, transition: CLOSE },
          active: { scale: [1, 1.3, 1, 14, 14], opacity: [1, 1, 1, 1, 0], transition: SEQUENCE },
        }}
        style={{ display: 'flex', transformOrigin: 'center' }}
      >
        <Favorite sx={{ fontSize: 42, color: accent, display: 'block' }} />
      </motion.div>
    </div>
  )
}
