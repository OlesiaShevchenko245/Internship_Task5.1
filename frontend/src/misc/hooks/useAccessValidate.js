import { useCallback, useContext } from 'react';
import { AuthoritiesContext } from '../providers/AuthoritiesProvider';

export const authModes = {
  ALL: 'ALL',
  ANY: 'ANY',
};

const functionsToModes = {
  [authModes.ANY]: (
    authorities,
    ownedAuthorities,
  ) => authorities.some((auth) => ownedAuthorities.includes(auth)),
  [authModes.ALL]: (
    authorities,
    ownedAuthorities,
  ) => authorities.every((auth) => ownedAuthorities.includes(auth)),
};

function useAccessValidate() {
  const ownedAuthorities = useContext(AuthoritiesContext);

  return useCallback(
    (neededAuthorities, mode = authModes.ANY) => {
      const authorities = (Array.isArray(neededAuthorities)
        ? neededAuthorities
        : [neededAuthorities]);
      return functionsToModes[mode](authorities, ownedAuthorities);
    },
    [ownedAuthorities]
  );
}

export default useAccessValidate;
