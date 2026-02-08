import React from 'react';
import { createUseStyles } from 'react-jss';
import MenuItemMUI from '@mui/material/MenuItem';
import useTheme from 'misc/hooks/useTheme';

const getClasses = createUseStyles(theme => ({
  container: {
    alignItems: 'center',
    display: 'flex',
    gap: `${theme.spacing(1)}px`,
  },
}));

const colorVariants = {
  header: 'header',
  primary: 'primary',
};

function MenuItem({
  children,
  colorVariant = colorVariants.primary,
  onClick,
  selected = false,
  value,
}) {
  const { theme } = useTheme();
  const classes = getClasses({ theme });
  return (
    <MenuItemMUI
      onClick={onClick}
      selected={selected}
      sx={{
        '&.MuiMenuItem-root': {
          '&:hover': {
            backgroundColor: theme.menuItem.color[colorVariant].backgroundHovered,
          },
          '&.Mui-selected': {
            backgroundColor: theme.menuItem.color[colorVariant].backgroundSelected,
          },
        },
      }}
      value={value}
    >
      <div className={classes.container}>
        {children}
      </div>
    </MenuItemMUI>
  );
}

export default MenuItem;
