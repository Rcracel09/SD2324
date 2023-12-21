package SD2324; 
 
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerData {
    private Map<String, User> usersMap= new HashMap<>();

    private ReentrantReadWriteLock lWRUser = new ReentrantReadWriteLock();

    

    public Boolean registerUserServer(String username, String password) {
        lWRUser.writeLock().lock();
        try {
            if(!usersMap.containsKey(username)) {
                usersMap.put(username, new User(username, password));
                return true;// return significa que registou com sucesso
            }
            else {
                return false; // return significa que não consegiu registar
            }
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public Boolean logInUserServer(String username, String password) {
        lWRUser.writeLock().lock();
        try {
            User userCheck = usersMap.get(username);
            if(usersMap.containsKey(username) && userCheck.getPassword().equals(password) ) {
                return true; // return significa que efetuou logIn com sucesso
            }
            else {
                return false; // return significa que aqueles dados de logIn não correspondem a um user
            }
        } finally {
            lWRUser.writeLock().unlock();
        }
    }
}
