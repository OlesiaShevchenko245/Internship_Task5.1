import React, {
  createContext,
} from 'react';
import { useSelector } from 'react-redux';

export const AuthoritiesContext = createContext([]);

const AuthoritiesProvider = ({
  children,
}) => {
  const {
    authorities,
  } = useSelector(({ user }) => user);
  return (
    <AuthoritiesContext.Provider value={authorities}>
      {children}
    </AuthoritiesContext.Provider>
  );
};

export default AuthoritiesProvider;
