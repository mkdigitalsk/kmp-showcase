import { CLOSE_SPEEDUP } from './closeTransition'

// Shared timing for the tablet-flip cards (banking, food). The open order is
// icon-flip then card-flip; the close is the exact reverse — card-flip then
// icon-flip — and runs a touch quicker.
export const ICON_FLIP_S = 0.32
export const CARD_FLIP_S = 0.52
export const ICON_FLIP_CLOSE_S = ICON_FLIP_S * CLOSE_SPEEDUP
export const CARD_FLIP_CLOSE_S = CARD_FLIP_S * CLOSE_SPEEDUP
export const FLIP_EASE = [0.4, 0, 0.2, 1] as const
