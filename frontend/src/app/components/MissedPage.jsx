import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';

import { DEFAULT_LOCATION_SEARCH } from './SearchParamsConfigurator';

function MissedPage({
  redirectPage,
}) {
  const location = useLocation();
  const search = location.search
    || `?${(new URLSearchParams(DEFAULT_LOCATION_SEARCH)).toString()}`;
  return (
    <Navigate to={`${redirectPage}${search}`} />
  );
}

export default MissedPage;
