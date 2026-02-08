import React from 'react';
import {
  IntlProvider as ReactIntlProvider,
} from 'react-intl';

import useLocationSearch from 'misc/hooks/useLocationSearch';
import { DEFAULT_LANGUAGE, locales } from 'misc/constants/languages';

function IntlProvider({
  children,
  messages,
}) {
  const {
    lang,
  } = useLocationSearch();

  return (
    <ReactIntlProvider
      defaultLocale={locales[DEFAULT_LANGUAGE]}
      locale={locales[lang]}
      messages={messages}
    >
      {children}
    </ReactIntlProvider>
  );
}

export default IntlProvider;
