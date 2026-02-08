import React from 'react';
import TypographyMUI from '@mui/material/Typography';

import useTheme from 'misc/hooks/useTheme';

const alignment = {
  center: 'center',
  inherit: 'inherit',
  justify: 'justify',
  left: 'left',
  right: 'right',
};

const colors = {
  primary: 'primary',
  secondary: 'secondary',
  tertiary: 'tertiary',
  error: 'error',
  info: 'info',
  success: 'success',
  warning: 'warning',
  inherit: 'inherit',
};

const variants = {
  caption: 'caption',
  default: 'default',
  subTitle: 'subTitle',
  title: 'title'
};

const Typography = ({
  align = alignment.inherit,
  capitalize,
  children,
  color = colors.primary,
  noWrap = false,
  variant = variants.default,
  wordBreak = 'normal',
}) => {
  const { theme } = useTheme();
  return (
    <TypographyMUI
      align={align}
      noWrap={noWrap}
      sx={{
        ...theme.typography.variants[variant],
        textTransform: capitalize && 'capitalize',
        caretColor: '#FFFFFF',
        color: color === 'inherit'
          ? 'inherit'
          : theme.typography.color[color] || color,
        wordBreak,
      }}
    >
      {children}
    </TypographyMUI>
  );
};

export default Typography;
