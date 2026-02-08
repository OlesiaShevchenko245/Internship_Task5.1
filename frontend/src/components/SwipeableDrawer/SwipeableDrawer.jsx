import React from 'react';
import SwipeableDrawerMUI from '@mui/material/SwipeableDrawer';

const SwipeableDrawer = ({
  anchor = 'left',
  children,
  isOpened,
  onClose,
}) => {
  return (
    <SwipeableDrawerMUI
      anchor={anchor}
      disableSwipeToOpen
      disableDiscovery
      disableBackdropTransition
      open={isOpened}
      onClose={onClose}
      onOpen={() => ({})}
    >
      {children}
    </SwipeableDrawerMUI>
  );
};

export default SwipeableDrawer;
