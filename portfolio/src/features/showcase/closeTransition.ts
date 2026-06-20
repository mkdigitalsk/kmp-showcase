import type { Transition } from 'motion/react'

// Reverse (close) animations run a touch quicker than open — a common UX standard.
export const CLOSE_SPEEDUP = 0.75

export function fasterClose(transition: Transition): Transition {
  const duration = (transition as { duration?: number }).duration
  return {
    ...transition,
    duration: duration != null ? duration * CLOSE_SPEEDUP : undefined,
    delay: 0,
  }
}
