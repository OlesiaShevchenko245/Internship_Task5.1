import React from 'react';
import { createUseStyles } from 'react-jss';
import CircularProgress from '../CircularProgress';
import IconError from '../icons/Error';
import IconNoData from '../icons/NoData';
import useTheme from 'misc/hooks/useTheme';

const getClasses = createUseStyles(theme => ({
  container: {
    alignItems: 'center',
    background: theme.colors.background.secondary,
    display: 'flex',
    flexDirection: 'column',
    gap: `${theme.spacing(2)}px`,
    height: '100%',
    justifyContent: 'center',
    minHeight: '200px',
    width: '100%',
  },
}));

const variants = {
  error: 'error',
  loading: 'loading',
  noData: 'noData',
};

const iconsToVariants = {
  [variants.error]: (<IconError size={56} />),
  [variants.loading]: (<CircularProgress size={56} />),
  [variants.noData]: (<IconNoData size={56} />),
};

function Loading({
  children,
  variant = variants.loading,
}) {
  const { theme } = useTheme();
  const classes = getClasses({ theme });
  return (
    <div className={classes.container}>
      {iconsToVariants[variant]}
      {children}
    </div>
  );
}

export default Loading;
