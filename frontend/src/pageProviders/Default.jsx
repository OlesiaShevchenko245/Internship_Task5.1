import DefaultPage from 'pages/default';
import React from 'react';

import PageContainer from './components/PageContainer';

const Default = (props) => {
  return (
    <PageContainer>
      <DefaultPage {...props} />
    </PageContainer>
  );
};

export default Default;
