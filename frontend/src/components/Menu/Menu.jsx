import React from 'react';
import MenuMUI from '@mui/material/Menu';
import useTheme from 'misc/hooks/useTheme';

const colorVariants = {
  header: 'header',
  primary: 'primary',
};

function Menu({
  anchorEl,
  children,
  colorVariant = colorVariants.primary,
  onClose,
  open,
}) {
  const { theme } = useTheme();
  return (
    <MenuMUI
      anchorEl={anchorEl}
      onClose={onClose}
      open={open}
      sx={{
        '& .MuiPaper-root': {
          background: theme.menu.color[colorVariant].background,
        },
      }}
    >
      {children}
    </MenuMUI>
  );
}

export default Menu;
