import { createUseStyles } from 'react-jss';
import { Link as InternalLink } from 'react-router-dom';
import classNames from 'classnames';
import ExternalLink from '@mui/material/Link';
import React from 'react';
import useLocationSearch from 'misc/hooks/useLocationSearch';

const getClasses = createUseStyles({
  noUnderline: {
    textDecoration: 'none',
  },
});

function Link({
  children,
  href = '',
  onClick,
  target,
  to,
  underline = false,
}) {
  const classes= getClasses();
  const locationSearch = useLocationSearch();
  const LinkComponent = href
    ? ExternalLink
    : InternalLink;
  const actualTo = to
    ? `${to.pathname}?${(new URLSearchParams(to.locationSearch
      || locationSearch)).toString()}`
    : '';
  return (
    <LinkComponent
      className={classNames(
        !underline && classes.noUnderline
      )}
      href={href}
      onClick={onClick}
      target={target}
      to={actualTo}
    >
      {children}
    </LinkComponent>
  );
}

export default Link;
