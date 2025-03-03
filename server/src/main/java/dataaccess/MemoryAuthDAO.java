package dataaccess;

import model.AuthData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of the AuthDAO interface
 */
public class MemoryAuthDAO implements AuthDAO {
    // store auth tokens in a map
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        // just clear the map
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        // add the auth to our map
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // return the auth from the map (might be null)
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // remove the auth from the map
        auths.remove(authToken);
    }
    
    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        // return all the values as a list
        return new ArrayList<>(auths.values());
    }
} 