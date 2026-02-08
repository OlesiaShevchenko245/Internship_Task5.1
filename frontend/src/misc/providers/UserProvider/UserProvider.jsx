import React, { createContext } from 'react';
import { useSelector } from 'react-redux';

export const UserContext = createContext({});

const UserProvider = ({
  children,
}) => {
  const user = useSelector(({ user }) => user);
  return (
    <UserContext.Provider
      value={{
        email: user.email,
        firstName: user.firstName,
        id: user.id,
        lastName: user.lastName,
        login: user.login,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

export default UserProvider;
