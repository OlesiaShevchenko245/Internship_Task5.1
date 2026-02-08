import { useContext, useMemo } from 'react';
import {
  ThemeContext,
} from '../providers/ThemeProvider';

const useTheme = () => {
  const {
    changeTheme,
    theme,
  } = useContext(ThemeContext);

  return useMemo(() => ({
    changeTheme,
    theme,
  }), [theme, changeTheme]);
};

export default useTheme;
