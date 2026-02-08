import React from 'react';
import SvgIcon from '../SvgIcon';
import useTheme from 'misc/hooks/useTheme';

/* eslint-disable max-len */
const NoData = ({
  color = 'default', // default | header | error | success | warning | info | <string>
  size = 32,
}) => {
  const { theme } = useTheme();
  const actualColor = theme.icon.color[color] || color;
  return (
    <SvgIcon
      style={{
        height: `${size}px`,
        width: `${size}px`,
      }}
      viewBox="0 0 24 24"
    >
      <path
        d="M15 4v2h3v12h-3v2h5V4zM4 20h5v-2H6V6h3V4H4z"
        fill={actualColor}
      />
    </SvgIcon>
  );
};

export default NoData;
