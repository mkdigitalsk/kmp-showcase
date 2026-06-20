'use client'

import { AccountBalance, AttachMoney } from '@mui/icons-material'
import { motion } from 'motion/react'
import { CARD_FLIP_CLOSE_S, ICON_FLIP_CLOSE_S, ICON_FLIP_S } from './flipTiming'

const OPEN = { duration: ICON_FLIP_S, ease: 'easeInOut' as const, delay: 0 }
// On close the icon waits for the (quicker) card flip back first.
const CLOSE = { duration: ICON_FLIP_CLOSE_S, ease: 'easeInOut' as const, delay: CARD_FLIP_CLOSE_S }

const faceStyle = {
  position: 'absolute' as const,
  inset: 0,
  display: 'grid',
  placeItems: 'center',
  backfaceVisibility: 'hidden' as const,
  WebkitBackfaceVisibility: 'hidden' as const,
}

interface BankFlipProps {
  accent: string
  flipSign?: 1 | -1
}

export function BankFlip({ accent, flipSign = -1 }: BankFlipProps) {
  const flipTo = 180 * flipSign

  return (
    <div style={{ width: 76, height: 76, perspective: 300 }}>
      <motion.div
        variants={{ rest: { rotateX: 0, transition: CLOSE }, active: { rotateX: flipTo, transition: OPEN } }}
        style={{ position: 'relative', width: '100%', height: '100%', transformStyle: 'preserve-3d' }}
      >
        <div style={faceStyle}>
          <AccountBalance sx={{ fontSize: 38, color: accent }} />
        </div>
        <div style={{ ...faceStyle, transform: `rotateX(${flipTo}deg)` }}>
          <AttachMoney sx={{ fontSize: 42, color: accent }} />
        </div>
      </motion.div>
    </div>
  )
}
