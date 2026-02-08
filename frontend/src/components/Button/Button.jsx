import React from 'react';
import ButtonMUI from '@mui/material/Button';
import useTheme from 'misc/hooks/useTheme';
import CircularProgress from '../CircularProgress';

const colorVariants = {
  header: 'header',
  primary: 'primary',
  secondary: 'secondary',
};

const variants = {
  primary: 'primary',
  secondary: 'secondary',
  text: 'text',
};

const MUIVariantsToVariants = {
  [variants.primary]: 'contained',
  [variants.secondary]: 'outlined',
  [variants.text]: 'text',
};

function Button({
  children,
  colorVariant = colorVariants.primary,
  disabled,
  endIcon,
  isLoading = false,
  onClick,
  startIcon,
  variant = variants.secondary,
}) {
  const { theme } = useTheme();
  return (
    <>
      {isLoading && (
        <ButtonMUI
          disabled
          variant="contained"
        >
          <CircularProgress size={16} />
        </ButtonMUI>
      )}
      {!isLoading && (
        <ButtonMUI
          disabled={disabled}
          endIcon={endIcon}
          onClick={onClick}
          startIcon={startIcon}
          sx={{
            '&.MuiButton-root': {
              background: theme.button.color[colorVariant].background,
              borderColor: theme.button.color[colorVariant].text,
              color: theme.button.color[colorVariant].text,
              opacity: disabled && '0.4',
              textTransform: 'none',
              '&:hover': {
                background: theme.button.color[colorVariant].backgroundHovered,
              },
              '&.Mui-disabled': {
                background: theme.button.color[colorVariant].backgroundDisabled,
              },
            },
          }}
          variant={MUIVariantsToVariants[variant]}
        >
          {children}
        </ButtonMUI>
      )}
    </>
  );
}

export default Button;
