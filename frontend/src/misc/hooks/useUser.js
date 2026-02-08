import { useContext } from 'react';
import { UserContext } from 'misc/providers/UserProvider';

/**
@return {
  email,
  firstName,
  id,
  lastName,
  login,
}
**/

const useUser = () => useContext(UserContext);

export default useUser;
