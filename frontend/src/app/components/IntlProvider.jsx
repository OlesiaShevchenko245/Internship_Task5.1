import React, { useMemo } from 'react';
import getMessages from 'intl';
import MiscIntlProvider from 'misc/providers/IntlProvider';
import useLocationSearch from 'misc/hooks/useLocationSearch';

function IntlProvider({
  children,
}) {
  const {
    lang,
  } = useLocationSearch();
  const messages = useMemo(() => getMessages(lang), [lang]);
  return (
    <MiscIntlProvider messages={messages}>
      {children}
    </MiscIntlProvider>
  )
}

export default IntlProvider;
