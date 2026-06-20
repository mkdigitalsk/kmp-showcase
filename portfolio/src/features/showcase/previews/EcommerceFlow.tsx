'use client'

import Box from '@mui/material/Box'
import { AnimatePresence, motion, useReducedMotion } from 'motion/react'
import { useTranslations } from 'next-intl'
import { useEffect, useState } from 'react'
import { PreviewScreen, type PreviewProps } from './PreviewKit'

// E-commerce micro-theme: Uber Eats / Shopify vibe — light type, generous whitespace,
// minimal palette so the product stands out, big emoji thumbs. Accent only touches the
// price, the "Add to cart" button and the cart badge. Auto-simulates a tap on the 2nd
// product -> navigates to the product detail (badge bumps 2 -> 3) and loops.

const R = 11 // medium radius

const PRODUCTS = [
  { key: 'runningShoes', emoji: '👟', price: '€89.00' },
  { key: 'headphones', emoji: '🎧', price: '€149.00' },
  { key: 'smartWatch', emoji: '⌚', price: '€199.00' },
]

const CHOSEN = PRODUCTS[1]

export function EcommerceFlow({ accent }: PreviewProps) {
  const t = useTranslations('previews.ecommerce')
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

  const cartCount = screen === 1 ? 3 : 2

  return (
    <PreviewScreen>
      <Box sx={{ position: 'relative', flex: 1, overflow: 'hidden' }}>
        <AnimatePresence mode="wait">
          {screen === 0 ? (
            <motion.div
              key="shop"
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: -10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '0 14px' }}
            >
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                <Box
                  sx={{
                    flex: 1,
                    px: 1.25,
                    py: 0.75,
                    borderRadius: `${R}px`,
                    bgcolor: 'action.hover',
                    fontSize: 11,
                    color: 'text.secondary',
                  }}
                >
                  🔍 {t('searchProducts')}
                </Box>
                <Box
                  sx={{
                    position: 'relative',
                    width: 32,
                    height: 32,
                    borderRadius: `${R}px`,
                    display: 'grid',
                    placeItems: 'center',
                    bgcolor: 'action.hover',
                    fontSize: 14,
                  }}
                >
                  🛒
                  <Box
                    sx={{
                      position: 'absolute',
                      top: -4,
                      right: -4,
                      minWidth: 15,
                      height: 15,
                      px: 0.25,
                      borderRadius: '50%',
                      display: 'grid',
                      placeItems: 'center',
                      bgcolor: accent,
                      color: 'common.white',
                      fontSize: 9.5,
                      fontWeight: 800,
                    }}
                  >
                    {cartCount}
                  </Box>
                </Box>
              </Box>
              {PRODUCTS.map((p, i) => (
                <Box
                  key={p.key}
                  sx={{
                    position: 'relative',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 1.25,
                    py: 1,
                  }}
                >
                  <Box
                    sx={{
                      width: 38,
                      height: 38,
                      borderRadius: `${R}px`,
                      display: 'grid',
                      placeItems: 'center',
                      bgcolor: 'action.hover',
                      fontSize: 20,
                      flexShrink: 0,
                    }}
                  >
                    {p.emoji}
                  </Box>
                  <Box sx={{ flex: 1, fontSize: 11.5, fontWeight: 600, color: 'text.primary' }}>{t(p.key)}</Box>
                  <Box sx={{ fontSize: 11.5, fontWeight: 800, color: accent, fontVariantNumeric: 'tabular-nums' }}>
                    {p.price}
                  </Box>
                  {tapping && i === 1 && !reduceMotion && (
                    <motion.div
                      initial={{ scale: 0, opacity: 0.5 }}
                      animate={{ scale: 2.4, opacity: 0 }}
                      transition={{ duration: 0.45 }}
                      style={{
                        position: 'absolute',
                        left: 19,
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
              key="product"
              initial={{ opacity: 0, x: 10 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, x: 10 }}
              transition={{ duration: 0.3 }}
              style={{ padding: '0 14px' }}
            >
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 0.5 }}>
                <Box
                  sx={{
                    position: 'relative',
                    width: 32,
                    height: 32,
                    borderRadius: `${R}px`,
                    display: 'grid',
                    placeItems: 'center',
                    bgcolor: 'action.hover',
                    fontSize: 14,
                  }}
                >
                  🛒
                  <motion.div
                    key={cartCount}
                    initial={reduceMotion ? false : { scale: 0.4 }}
                    animate={reduceMotion ? undefined : { scale: 1 }}
                    transition={{ type: 'spring', stiffness: 500, damping: 18 }}
                    style={{
                      position: 'absolute',
                      top: -4,
                      right: -4,
                      minWidth: 15,
                      height: 15,
                      borderRadius: '50%',
                      display: 'grid',
                      placeItems: 'center',
                      background: accent,
                      color: '#fff',
                      fontSize: 9.5,
                      fontWeight: 800,
                    }}
                  >
                    {cartCount}
                  </motion.div>
                </Box>
              </Box>
              <Box
                sx={{
                  height: 84,
                  borderRadius: `${R}px`,
                  display: 'grid',
                  placeItems: 'center',
                  bgcolor: 'action.hover',
                  fontSize: 48,
                  mb: 1.25,
                }}
              >
                {CHOSEN.emoji}
              </Box>
              <Box sx={{ fontSize: 14, fontWeight: 700, color: 'text.primary', letterSpacing: '-0.01em' }}>
                {t(CHOSEN.key)}
              </Box>
              <Box sx={{ fontSize: 16, fontWeight: 800, color: accent, mt: 0.25, fontVariantNumeric: 'tabular-nums' }}>
                {CHOSEN.price}
              </Box>
              <Box sx={{ fontSize: 10, color: 'text.secondary', mt: 0.5, mb: 1.25 }}>★★★★☆ 124 {t('reviews')}</Box>
              <Box
                sx={{
                  width: '100%',
                  py: 1,
                  borderRadius: `${R}px`,
                  display: 'grid',
                  placeItems: 'center',
                  bgcolor: accent,
                  color: 'common.white',
                  fontSize: 12,
                  fontWeight: 700,
                }}
              >
                {t('addToCart')}
              </Box>
            </motion.div>
          )}
        </AnimatePresence>
      </Box>
    </PreviewScreen>
  )
}
