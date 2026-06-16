import { Chip as MuiChip, type ChipProps as MuiChipProps } from '@mui/material'
import { type ElementType } from 'react'

// Allow the chip to render as a link (component="a" + href) for showcase links,
// while keeping the simple wrapper API for plain chips.
type ChipProps = MuiChipProps & {
  component?: ElementType
  href?: string
  target?: string
  rel?: string
}

export function Chip({ sx, ...props }: ChipProps) {
  return <MuiChip sx={sx} {...(props as MuiChipProps)} />
}

type FilterChipProps = Omit<MuiChipProps, 'variant' | 'color'> & {
  selected: boolean
}

export function FilterChip({ selected, sx, ...props }: FilterChipProps) {
  return (
    <MuiChip
      variant={selected ? 'filled' : 'outlined'}
      color={selected ? 'primary' : 'default'}
      sx={sx}
      {...props}
    />
  )
}
