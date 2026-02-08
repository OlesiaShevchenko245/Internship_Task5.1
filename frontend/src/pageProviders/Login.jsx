import * as pages from 'constants/pages';
import { useSelector } from 'react-redux';
import LoginPage from 'pages/login';
import pagesURLs from 'constants/pagesURLs';
import React, { useEffect } from 'react';
import useChangePage from 'misc/hooks/useChangePage';
import useLocationSearch from 'misc/hooks/useLocationSearch';

import PageContainer from './components/PageContainer';

const Login = (props) => {
  const locationSearch = useLocationSearch();
  const user = useSelector(({ user }) => user);
  const changePage = useChangePage();

  useEffect(() => {
    if (user.isAuthorized) {
      changePage({
        locationSearch: locationSearch.redirectLocationSearch
          ? JSON.parse(locationSearch.redirectLocationSearch)
          : locationSearch,
        pathname: locationSearch.redirectPathname
          || `${pagesURLs[pages.defaultPage]}`,
        replace: true,
      });
    }
  }, [user.isAuthorized]);

  return (
    <PageContainer>
      {user.isAuthorized
        ? null
        : (
          <LoginPage {...props} />
        )}
    </PageContainer>
  );
};

export default Login;
