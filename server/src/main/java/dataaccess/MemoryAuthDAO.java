package dataaccess;

import model.AuthData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Keeps track of auth tokens in memory
 */
public class MemoryAuthDAO implements AuthDAO {
    // map of auth tokens -> auth data
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        // bye bye tokens
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        // store the new token
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // null if token doesn't exist
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // remove on logout
        auths.remove(authToken);
    }
    
    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        // not sure if we'll need this but the interface wants it
        return new ArrayList<>(auths.values());
    }
} 