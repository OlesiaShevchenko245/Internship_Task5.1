import { useLocation, useNavigate } from 'react-router-dom';
import useLocationSearch from 'misc/hooks/useLocationSearch';

function useChangePage() {
  const locationSearch = useLocationSearch();
  const location = useLocation();
  const navigate = useNavigate();
  return ({
    locationSearch: inputLocationSearch,
    pathname,
    replace = false,
  }) => {
    navigate(
      {
        pathname: pathname || location.pathname,
        search: `?${new URLSearchParams(inputLocationSearch || locationSearch)
          .toString()}`,
      },
      {
        replace,
      }
    );
  };
}

export default useChangePage;
