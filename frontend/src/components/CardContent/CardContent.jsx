import { createUseStyles } from 'react-jss';
import React from 'react';
import classNames from 'classnames';
import useTheme from 'misc/hooks/useTheme';

const getClasses = createUseStyles(theme => ({
  container: {
    display: 'flex',
    flexDirection: 'column',
    overflowX: 'auto',
  },
  padding: {
    padding: `${theme.spacing(0)}px ${theme.spacing(2)}px`,
  },
}));

function CardContent({
  children,
  disablePadding = false,
}) {
  const { theme } = useTheme();
  const classes = getClasses({ theme });
  return (
    <div
      className={classNames(
        classes.container,
        disablePadding ? '' : classes.padding
      )}
    >
      {children}
    </div>
  );
}

export default CardContent;
