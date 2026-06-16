import { GitHub, LinkedIn, WhatsApp } from '@mui/icons-material'
import Box from '@mui/material/Box'
import IconButton from '@mui/material/IconButton'
import { TextBody1Neutral60 } from '../text'

const socials = [
  { label: 'LinkedIn', href: 'https://linkedin.com/in/miroslavkusnir', Icon: LinkedIn },
  { label: 'WhatsApp', href: 'https://wa.me/421951892670', Icon: WhatsApp },
  { label: 'GitHub', href: 'https://github.com/KusnirM', Icon: GitHub },
]

export function Footer() {
  return (
    <Box component="footer" sx={{ borderTop: '1px solid', borderColor: 'divider' }}>
      <Box
        sx={{
          maxWidth: 880,
          mx: 'auto',
          px: 3,
          py: 4,
          display: 'flex',
          flexWrap: 'wrap',
          gap: 2,
          alignItems: 'center',
          justifyContent: 'space-between',
        }}
      >
        <TextBody1Neutral60>© 2026 MK Digital s.r.o. · Miroslav Kušnír</TextBody1Neutral60>
        <Box sx={{ display: 'flex', gap: 0.5 }}>
          {socials.map(({ label, href, Icon }) => (
            <IconButton
              key={label}
              component="a"
              href={href}
              target="_blank"
              rel="noopener noreferrer"
              aria-label={label}
              size="small"
            >
              <Icon fontSize="small" />
            </IconButton>
          ))}
        </Box>
      </Box>
    </Box>
  )
}
