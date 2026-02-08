import React, {
  createContext,
  useCallback,
  useMemo,
  useState,
} from 'react';

import defaultTheme from './themes/default';

const themeNames = {
  default: 'default',
};

const ThemesToThemeNames = {
  [themeNames.default]: defaultTheme,
};

export const ThemeContext = createContext({
  changeTheme: () => {},
  theme: {},
});

const ThemeProvider = ({
  children,
  themeName: inputThemeName = themeNames.default,
}) => {
  const [state, setState] = useState({
    themeName: inputThemeName,
  });

  const changeTheme = useCallback((themeName) => {
    setState(prevState => ({
      ...prevState,
      themeName,
    }));
  }, []);

  const contextValue = useMemo(() => ({
    changeTheme,
    theme: ThemesToThemeNames[state.themeName] || defaultTheme,
  }), [state.themeName, changeTheme]);

  return (
    <ThemeContext.Provider value={contextValue}>
      {children}
    </ThemeContext.Provider>
  );
};

export default ThemeProvider;
