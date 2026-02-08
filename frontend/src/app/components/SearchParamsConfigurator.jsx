import { useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import languages, { DEFAULT_LANGUAGE } from 'misc/constants/languages';

const locationSearch = {
  lang: 'lang',
};

export const DEFAULT_LOCATION_SEARCH = {
  [locationSearch.lang]: DEFAULT_LANGUAGE,
};

function SearchParamsConfigurator() {
  const [searchParams, setSearchParams] = useSearchParams();

  useEffect(() => {
    let isSearchParamsUpdated = false;
    if (!searchParams.has(locationSearch.lang)
      || !Object.values(languages)
        .includes(searchParams.get(locationSearch.lang) || '')
    ) {
      searchParams.set(locationSearch.lang, DEFAULT_LANGUAGE);
      isSearchParamsUpdated = true;
    }
    if (isSearchParamsUpdated) {
      setSearchParams(searchParams, { replace: true });
    }
  }, [searchParams]);
}

export default SearchParamsConfigurator;
