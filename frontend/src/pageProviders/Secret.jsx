import * as authorities from 'constants/authorities';
import SecretePage from 'pages/secret';
import React from 'react';

import PageAccessValidator from './components/PageAccessValidator';
import PageContainer from './components/PageContainer';

const Secret = (props) => {
  return (
    <PageAccessValidator
      neededAuthorities={[authorities.ENABLE_SEE_SECRET_PAGE]}
    >
      <PageContainer>
        <SecretePage {...props} />
      </PageContainer>
    </PageAccessValidator>
  );
};

export default Secret;
