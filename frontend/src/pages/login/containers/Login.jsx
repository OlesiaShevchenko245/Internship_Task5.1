import { createUseStyles } from 'react-jss';
import { useIntl } from 'react-intl';
import useTheme from 'misc/hooks/useTheme';
import Button from 'components/Button';
import Card from 'components/Card';
import CardActions from 'components/CardActions';
import CardContent from 'components/CardContent';
import CardTitle from 'components/CardTitle';
import Dialog from 'components/Dialog';
import IconButton from 'components/IconButton';
import IconClose from 'components/icons/Close';
import IconVisibilityOff from 'components/icons/VisibilityOff';
import IconVisibilityOn from 'components/icons/VisibilityOn';
import md5 from 'md5';
import React, { useEffect, useState } from 'react';
import TextField from 'components/TextField';
import Typography from 'components/Typography';

import * as errorCodes from '../constants/errorCodes';

const getClasses = createUseStyles((theme) => ({
  buttons: {
    display: 'flex',
    gap: `${theme.spacing(1)}px`,
    justifyContent: 'center',
  },
  container: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  content: {
    display: 'flex',
    flexDirection: 'column',
    gap: `${theme.spacing(2)}px`,
    width: '300px',
  },
  dialogContent: {
    display: 'flex',
    flexDirection: 'column',
    gap: `${theme.spacing(2)}px`,
  },
}));

const errorTypes = {
  EMPTY_SIGN_UP_EMAIL: 'EMPTY_SIGN_UP_EMAIL',
  EMPTY_SIGN_UP_LOGIN: 'EMPTY_SIGN_UP_LOGIN',
  EMPTY_SIGN_UP_PASSWORD: 'EMPTY_SIGN_UP_PASSWORD',
  EMPTY_SIGN_UP_PASSWORD_CONFIRM: 'EMPTY_SIGN_UP_PASSWORD_CONFIRM',
  INVALID_EMAIL: 'INVALID_EMAIL',
  PASSWORDS_MISMATCHES: 'PASSWORDS_MISMATCHES',
};

const isEmail = (text) => {
  const re = /\S+@\S+\.\S+/;
  return re.test(text.toLowerCase());
};

function Login({
  errors, // [{ code: <string>, description: <string> }]
  isAutoSignInAfterSignUp = true,
  isFailedSignIn,
  isFailedSignUp,
  isFetchingSignIn,
  isFetchingSignUp,
  onSignIn,
  onSignUp,
}) {
  const { formatMessage } = useIntl();
  const { theme } = useTheme();
  const classes = getClasses({ theme });
  const [state, setState] = useState({
    emailOrLogin: '',
    externalErrors: [],
    password: '',
    isShowPassword: false,
    isSignUpDialogOpened: false,
    signInValidationErrors: [],
    signUpEmail: '',
    signUpFirstName: '',
    signUpLastName: '',
    signUpLogin: '',
    signUpPassword: '',
    signUpPasswordConfirm: '',
    signUpValidationErrors: [],
  });

  const onCancelSignUp = () => setState({
    ...state,
    externalErrors: [],
    signUpEmail: '',
    signUpFirstName: '',
    signUpLastName: '',
    signUpLogin: '',
    signUpPassword: '',
    signUpPasswordConfirm: '',
    signUpValidationErrors: [],
    isSignUpDialogOpened: false,
  });

  const getSignUpValidationErrors = () => {
    const errors = [];
    if (!state.signUpEmail) {
      errors.push(errorTypes.EMPTY_SIGN_UP_EMAIL);
    } else if (!isEmail(state.signUpEmail)) {
      errors.push(errorTypes.INVALID_EMAIL);
    }
    if (!state.signUpLogin) {
      errors.push(errorTypes.EMPTY_SIGN_UP_LOGIN);
    }
    if (!state.signUpPassword) {
      errors.push(errorTypes.EMPTY_SIGN_UP_PASSWORD);
    }
    if (!state.signUpPasswordConfirm) {
      errors.push(errorTypes.EMPTY_SIGN_UP_PASSWORD_CONFIRM);
    }
    if (state.signUpPassword !== state.signUpPasswordConfirm) {
      errors.push(errorTypes.PASSWORDS_MISMATCHES);
    }
    return errors;
  };

  useEffect(() => {
    const errorCodeValues = Object.values(errorCodes);
    const messages = errors.map(error => errorCodeValues.includes(error.code)
      ? formatMessage({ id: `error.${error.code}` })
      : error.description);
    setState({
      ...state,
      externalErrors: messages,
    })
  }, [errors]);

  useEffect(() => {
    if (state.isSignUpDialogOpened && !isFetchingSignUp && !isFailedSignUp) {
      if (isAutoSignInAfterSignUp) {
        onSignIn({
          login: state.signUpLogin,
          password: md5(state.signUpPassword),
        });
      }
      onCancelSignUp();
    }
  }, [isFetchingSignUp, isFailedSignUp]);

  return (
    <div className={classes.container}>
      <div className={classes.content}>
        <TextField
          label={formatMessage({ id: 'field.loginOrEmail' })}
          onChange={({ target }) => setState({
            ...state,
            emailOrLogin: target.value,
          })}
          value={state.emailOrLogin}
        />
        <TextField
          AdornmentEnd={(
            <IconButton
              colorVariant="secondary"
              onPress={() => setState({
                ...state,
                isShowPassword: true,
              })}
              onRelease={() => setState({
                ...state,
                isShowPassword: false,
              })}
            >
              {state.isShowPassword
                ? <IconVisibilityOn size={24} />
                : <IconVisibilityOff size={24} />
              }
            </IconButton>
          )}
          label={formatMessage({ id: 'field.password' })}
          inputType={state.isShowPassword ? 'text' : 'password'}
          onChange={({ target }) => setState({
            ...state,
            password: target.value,
          })}
          value={state.password}
        />
        {isFailedSignIn && state.externalErrors.map(errorMessage => (
          <Typography color="error">
            {errorMessage}
          </Typography>
        ))}
        <div className={classes.buttons}>
          <Button
            colorVariant="secondary"
            onClick={() => setState({
              ...state,
              isSignUpDialogOpened: true,
            })}
            variant="secondary"
          >
            <Typography>
              {formatMessage({ id: 'signUp' })}
            </Typography>
          </Button>
          <Button
            disabled={!state.emailOrLogin || !state.password}
            isLoading={isFetchingSignIn}
            onClick={() => {
              const withEmail = isEmail(state.emailOrLogin);
              onSignIn({
                email: withEmail ? state.emailOrLogin : null,
                login: withEmail ? null : state.emailOrLogin,
                password: md5(state.password)
              });
            }}
            variant="primary"
          >
            <Typography color="inherit">
              <strong>
                {formatMessage({ id: 'signIn' })}
              </strong>
            </Typography>
          </Button>
        </div>
      </div>
      <Dialog
        maxWidth="xs"
        open={state.isSignUpDialogOpened}
      >
        <Card>
          {isFailedSignUp && !!state.externalErrors.length && (
            <Card variant="error">
              <CardTitle>
                {state.externalErrors.map(errorMessage => (
                  <Typography color="error">
                    {errorMessage}
                  </Typography>
                ))}
              </CardTitle>
            </Card>
          )}
          <CardTitle>
            <Typography variant="subTitle">
              {formatMessage({ id: 'signUp' })}
            </Typography>
            <IconButton onClick={onCancelSignUp}>
              <IconClose size={24} />
            </IconButton>
          </CardTitle>
          <CardContent>
            <div className={classes.dialogContent}>
              <TextField
                helperText={state.signUpValidationErrors
                  .includes(errorTypes.EMPTY_SIGN_UP_LOGIN)
                && formatMessage({
                  id: `signUp.error.${errorTypes.EMPTY_SIGN_UP_LOGIN}`,
                })}
                isError={state.signUpValidationErrors
                  .includes(errorTypes.EMPTY_SIGN_UP_LOGIN)}
                label={formatMessage({ id: 'field.login' })}
                onChange={({ target }) => setState({
                  ...state,
                  signUpLogin: target.value,
                })}
                required
                value={state.signUpLogin}
              />
              <TextField
                helperText={[
                  errorTypes.EMPTY_SIGN_UP_EMAIL,
                  errorTypes.INVALID_EMAIL,
                ]
                  .filter(errorType => state.signUpValidationErrors
                    .includes(errorType))
                  .map(errorType => formatMessage({
                    id: `signUp.error.${errorType}`
                  }))
                  .join('; ')}
                isError={[
                  errorTypes.EMPTY_SIGN_UP_EMAIL,
                  errorTypes.INVALID_EMAIL,
                ].some(errorType => state.signUpValidationErrors
                  .includes(errorType))}
                label={formatMessage({ id: 'field.email' })}
                onChange={({ target }) => setState({
                  ...state,
                  signUpEmail: target.value,
                })}
                required
                value={state.signUpEmail}
              />
              <TextField
                label={formatMessage({ id: 'field.firstName' })}
                onChange={({ target }) => setState({
                  ...state,
                  signUpFirstName: target.value,
                })}
                value={state.signUpFirstName}
              />
              <TextField
                label={formatMessage({ id: 'field.lastName' })}
                onChange={({ target }) => setState({
                  ...state,
                  signUpLastName: target.value,
                })}
                value={state.signUpLastName}
              />
              <TextField
                AdornmentEnd={(
                  <IconButton
                    colorVariant="secondary"
                    onPress={() => setState({
                      ...state,
                      isShowPassword: true,
                    })}
                    onRelease={() => setState({
                      ...state,
                      isShowPassword: false,
                    })}
                  >
                    {state.isShowPassword
                      ? <IconVisibilityOn size={24} />
                      : <IconVisibilityOff size={24} />
                    }
                  </IconButton>
                )}
                helperText={[
                  errorTypes.EMPTY_SIGN_UP_PASSWORD,
                ]
                  .filter(errorType => state.signUpValidationErrors
                    .includes(errorType))
                  .map(errorType => formatMessage({
                    id: `signUp.error.${errorType}`
                  }))
                  .join('; ')}
                label={formatMessage({ id: 'field.password' })}
                inputType={state.isShowPassword ? 'text' : 'password'}
                isError={[
                  errorTypes.EMPTY_SIGN_UP_PASSWORD,
                ].some(errorType => state.signUpValidationErrors
                  .includes(errorType))}
                onChange={({ target }) => setState({
                  ...state,
                  signUpPassword: target.value,
                })}
                required
                value={state.signUpPassword}
              />
              <TextField
                helperText={[
                  errorTypes.EMPTY_SIGN_UP_PASSWORD_CONFIRM,
                  errorTypes.PASSWORDS_MISMATCHES,
                ]
                  .filter(errorType => state.signUpValidationErrors
                    .includes(errorType))
                  .map(errorType => formatMessage({
                    id: `signUp.error.${errorType}`
                  }))
                  .join('; ')}
                label={formatMessage({ id: 'field.passwordConfirm' })}
                inputType="password"
                isError={[
                  errorTypes.EMPTY_SIGN_UP_PASSWORD_CONFIRM,
                  errorTypes.PASSWORDS_MISMATCHES,
                ].some(errorType => state.signUpValidationErrors
                  .includes(errorType))}
                onChange={({ target }) => setState({
                  ...state,
                  signUpPasswordConfirm: target.value,
                })}
                required
                value={state.signUpPasswordConfirm}
              />
            </div>
          </CardContent>
          <CardActions>
            <Button
              colorVariant="secondary"
              onClick={onCancelSignUp}
              variant="secondary"
            >
              <Typography>
                {formatMessage({ id: 'btn.cancel' })}
              </Typography>
            </Button>
            <Button
              isLoading={isFetchingSignUp}
              onClick={() => {
                const validationErrors = getSignUpValidationErrors();
                if (!validationErrors.length) {
                  onSignUp({
                    email: state.signUpEmail,
                    firstName: state.signUpFirstName,
                    lastName: state.signUpLastName,
                    login: state.signUpLogin,
                    password: md5(state.signUpPassword),
                  });
                }
                setState({
                  ...state,
                  signUpValidationErrors: validationErrors,
                });
              }}
              variant="primary"
            >
              <Typography color="inherit">
                <strong>
                  {formatMessage({ id: 'btn.signUp.apply' })}
                </strong>
              </Typography>
            </Button>
          </CardActions>
        </Card>
      </Dialog>
    </div>
  );
}

export default Login;
