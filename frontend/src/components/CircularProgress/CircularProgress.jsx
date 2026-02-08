import CircularProgressMUI from '@mui/material/CircularProgress';
import React from 'react';
import useTheme from 'misc/hooks/useTheme';

function CircularProgress({
  size = 24,
}) {
  const { theme } = useTheme();
  return (
    <CircularProgressMUI
      size={size}
      sx={{
        colorPrimary: theme.circularProgress.color,
      }}
      thickness={3}
    />
  );
}

export default CircularProgress;
