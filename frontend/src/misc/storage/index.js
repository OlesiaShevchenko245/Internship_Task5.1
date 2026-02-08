const getItem = (key) => {
  return localStorage.getItem(key);
};

const removeItem = (key) => {
  localStorage.removeItem(key);
};

const setItem = (key, value) => {
  localStorage.setItem(key, value);
};

export const keys = {
  TOKEN: 'TOKEN',
  TOKEN_EXPIRATION: 'TOKEN_EXP',
};

const forExport = {
  getItem,
  removeItem,
  setItem,
};

export default forExport;
