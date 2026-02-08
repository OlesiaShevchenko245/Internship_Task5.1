import axios from 'axios';
import storage, { keys } from '../storage';

axios.interceptors.request.use((params) => {
  const token = storage.getItem(keys.TOKEN);
  if (token) {
    params.headers.setAuthorization(`Bearer ${token}`);
  }
  return params;
});

const addAxiosInterceptors = ({
  onSignOut,
}) => {
  axios.interceptors.response.use(
    (response) => response.data,
    (error) => {
      if (error.response.data
        .some(beError => beError?.code === 'INVALID_TOKEN')
      ) {
        onSignOut();
      }
      throw error.response.data;
    }
  );
};

export {
  addAxiosInterceptors,
};

export default axios;
