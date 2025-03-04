package dataaccess;

import model.AuthData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages authentication tokens in memory
 */
public class MemoryAuthDAO implements AuthDAO {
    private static MemoryAuthDAO instance;
    private final Map<String, AuthData> auths = new HashMap<>();
    
    private MemoryAuthDAO() {}
    
    public static synchronized MemoryAuthDAO getInstance() {
        if (instance == null) {
            instance = new MemoryAuthDAO();
        }
        return instance;
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }
    
    @Override
    public List<AuthData> getAllAuthData() throws DataAccessException {
        return new ArrayList<>(auths.values());
    }
} 