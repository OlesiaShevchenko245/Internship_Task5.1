import { createStore, applyMiddleware } from 'redux';
import { thunk } from 'redux-thunk';

const createStoreWithMiddleware = applyMiddleware(thunk)(createStore);

export default function configureStore(rootReducer, initialState) {
  return createStoreWithMiddleware(rootReducer, initialState);
}
