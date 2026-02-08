import React, { useEffect, useState, useMemo } from 'react';
import { useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import * as pages from 'constants/pages';
import pagesURLs from 'constants/pagesURLs';
import useAccessValidate, { authModes } from 'misc/hooks/useAccessValidate';
import useChangePage from 'misc/hooks/useChangePage';
import useLocationSearch from 'misc/hooks/useLocationSearch';

function PageAccessValidator({
  neededAuthorities = [],
  children,
  mode = authModes.ANY,
}) {
  const changePage = useChangePage();
  const location = useLocation();
  const locationSearch = useLocationSearch();
  const validateAccess = useAccessValidate();

  const {
    isFetchingSignIn,
    isFetchingUser,
    isAuthorized,
  } = useSelector(({ user }) => user);

  const [state, setState] = useState({
    isValid: false,
  });

  const hasAccess = useMemo(
    () => validateAccess(neededAuthorities, mode),
    [neededAuthorities, validateAccess]
  );

  useEffect(() => {
    if (!isFetchingUser && !isFetchingSignIn) {
      if (!isAuthorized) {
        changePage({
          locationSearch: {
            ...locationSearch,
            redirectLocationSearch: locationSearch.redirectLocationSearch
              || JSON.stringify(locationSearch),
            redirectPathname: locationSearch.redirectPathname
              || location.pathname,
          },
          pathname: `${pagesURLs[pages.login]}`,
          replace: true,
        });
      } else if (!hasAccess) {
        changePage({
          pathname: `${pagesURLs[pages.defaultPage]}`,
        });
      } else {
        setState(prevState => ({
          ...prevState,
          isValid: true,
        }));
      }
    }
  }, [isFetchingUser, isAuthorized, hasAccess]);

  return (
    <>
      {state.isValid ? children : null}
    </>
  );
}

export default PageAccessValidator;
