import { createUseStyles } from 'react-jss';
import classNames from 'classnames';
import React from 'react';
import useTheme from 'misc/hooks/useTheme';

const getClasses = createUseStyles((theme) => ({
  container: {
    cursor: 'pointer',
    display: 'flex',
    height: '100%',
    userSelect: 'none',
    width: '100%',
  },
  hovered: {
    '&:hover': {
      background: theme.hover.background,
    },
  },
  hoveredLight: {
    '&:hover': {
      background: theme.hover.backgroundLight,
    },
  },
  selected: {
    background: theme.hover.selected.background,
  },
}));

function Hover({
  children,
  light = false,
  onClick,
  selected = false,
}) {
  const { theme } = useTheme();
  const classes = getClasses({ theme });

  return (
    <div
      className={classNames(
        classes.container,
        selected && classes.selected,
        light
          ? !selected && classes.hoveredLight
          : !selected && classes.hovered
      )}
      onClick={onClick}
    >
      {children}
    </div>
  );
}

export default Hover;
