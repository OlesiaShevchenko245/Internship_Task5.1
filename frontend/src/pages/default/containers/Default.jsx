import { useIntl } from 'react-intl';
import React from 'react';
import Typography from 'components/Typography';

function Default() {
  const { formatMessage } = useIntl();

  return (
    <Typography>
      {formatMessage({ id: 'title' })}
    </Typography>
  );
}

export default Default;
