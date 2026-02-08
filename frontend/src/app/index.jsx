import { Provider } from 'react-redux';
import configureStore from 'misc/redux/configureStore';
import React from 'react';

import App from './containers/App';
import rootReducer from './reducers';

const store = configureStore(rootReducer);
export default function Index() {
  return (
    <Provider store={store} >
      <App />
    </Provider>
  );
}
