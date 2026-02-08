import React from 'react';
import SvgIcon from '../SvgIcon';
import useTheme from 'misc/hooks/useTheme';

/* eslint-disable max-len */
const Menu = ({
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
      <g>
        <path
          d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"
          fill={actualColor}
        />
      </g>
    </SvgIcon>
  );
};

export default Menu;
