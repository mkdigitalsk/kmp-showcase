'use client'

import { DarkMode, LightMode, SettingsBrightness } from '@mui/icons-material'
import { IconButton, ListItemIcon, ListItemText, Menu, MenuItem, Tooltip } from '@mui/material'
import { useColorScheme } from '@mui/material/styles'
import { type MouseEvent, type ReactNode, useState } from 'react'

type Mode = 'system' | 'light' | 'dark'

const MODES: { value: Mode; label: string; icon: ReactNode }[] = [
  { value: 'system', label: 'System', icon: <SettingsBrightness fontSize="small" /> },
  { value: 'light', label: 'Light', icon: <LightMode fontSize="small" /> },
  { value: 'dark', label: 'Dark', icon: <DarkMode fontSize="small" /> },
]

export function ThemeModeToggle() {
  const { mode, setMode } = useColorScheme()
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)

  // `mode` is undefined during SSR / before hydration — render a stable
  // placeholder so server and first client render match (no hydration mismatch).
  if (!mode) {
    return (
      <IconButton size="small" disabled aria-label="Theme">
        <SettingsBrightness fontSize="small" />
      </IconButton>
    )
  }

  const current = MODES.find((m) => m.value === mode) ?? MODES[0]

  const handleOpen = (event: MouseEvent<HTMLElement>) => setAnchorEl(event.currentTarget)
  const handleClose = () => setAnchorEl(null)
  const handleSelect = (value: Mode) => {
    setMode(value)
    handleClose()
  }

  return (
    <>
      <Tooltip title="Theme">
        <IconButton size="small" onClick={handleOpen} aria-label="Theme">
          {current.icon}
        </IconButton>
      </Tooltip>
      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
        {MODES.map((m) => (
          <MenuItem key={m.value} selected={m.value === mode} onClick={() => handleSelect(m.value)}>
            <ListItemIcon>{m.icon}</ListItemIcon>
            <ListItemText>{m.label}</ListItemText>
          </MenuItem>
        ))}
      </Menu>
    </>
  )
}
