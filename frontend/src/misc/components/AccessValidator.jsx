import React, { useMemo } from 'react';

import useAccessValidate, { authModes } from '../hooks/useAccessValidate';

function AccessValidator({
  children,
  neededAuthorities,
  mode = authModes.ANY,
}) {
  const validateAccess = useAccessValidate();
  const hasAccess = useMemo(
    () => validateAccess(neededAuthorities, mode),
    [neededAuthorities, validateAccess]);
  return (
    <>
      {hasAccess ? children : null}
    </>
  );
}

export default AccessValidator;
