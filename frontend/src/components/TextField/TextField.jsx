import React, { useState, useMemo } from 'react';
import InputAdornmentMui from '@mui/material/InputAdornment';
import TextFieldMui from '@mui/material/TextField';
import Typography from '../Typography';
import useTheme from 'misc/hooks/useTheme';

const colorVariants = {
  header: 'header',
  primary: 'primary',
};

const inputTypes = {
  password: 'password',
  text: 'text',
};

const REQUIRED_CHAR = '*';

const TextField = ({
  AdornmentStart,
  AdornmentEnd,
  autoFocus = false,
  colorVariant = colorVariants.primary,
  disabled = false,
  helperText,
  inputType = inputTypes.text,
  isError = false,
  label,
  multiline = false,
  onBlur,
  onChange,
  onSelect,
  required = false,
  value,
}) => {
  const { theme } = useTheme();
  const [ state, setState ] = useState({
    isFocused: false,
  });
  const isEmptyValue = !value.length;
  const labelColor = useMemo(() => {
    let color;
    if (isError && !isEmptyValue) {
      color = theme.colors.text.error;
    } else if (state.isFocused || !isEmptyValue) {
      color = theme.input.color[colorVariant].text.secondary;
    } else {
      color = theme.input.color[colorVariant].placeholder;
    }
    return color;
  }, [isError, isEmptyValue, theme, state.isFocused]);

  return (
    <TextFieldMui
      autoFocus={autoFocus}
      disabled={disabled}
      error={isError}
      fullWidth
      helperText={helperText}
      InputProps={{
        endAdornment: AdornmentEnd && (
          <InputAdornmentMui position="end">
            {AdornmentEnd}
          </InputAdornmentMui>
        ),
        startAdornment: AdornmentStart && (
          <InputAdornmentMui position="start">
            {AdornmentStart}
          </InputAdornmentMui>
        ),
      }}
      label={(
        <Typography color={labelColor}>
          {required ? `${REQUIRED_CHAR}${label}` : label}
        </Typography>
      )}
      multiline={multiline}
      onBlur={() => setState({
        ...state,
        isFocused: false,
      })}
      onChange={onChange}
      onFocus={() => setState({
        ...state,
        isFocused: true,
      })}
      onSelect={onSelect}
      sx={{
        '& .MuiInputBase-root:before': {
          display: 'none',
        },
        '& .MuiInputBase-root:after': {
          display: 'none',
        },
        '& .MuiInputBase-root': {
          background: disabled && 'rgba(0, 0, 0, 0.05) !important',
          borderBottom: `1px solid ${isError
            ? theme.colors.text.error
            : theme.input.color[colorVariant].border}`,
          color: theme.input.color[colorVariant].text.primary,
          opacity: disabled && '0.4',
          marginTop: `${theme.spacing(1.5)}px`,
          '&:hover': !disabled
            ? {
              marginBottom: '-0.5px !important',
              borderBottom: `2px solid ${isError
                ? theme.colors.text.error
                : theme.input.color[colorVariant].border}`,
            }
            : {},
        },
      }}
      type={inputType}
      value={value}
      variant="standard"
    />
  );
};

export default TextField;
