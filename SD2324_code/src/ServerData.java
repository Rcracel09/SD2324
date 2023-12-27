import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.*;

public class ServerData {
    private Map<String, User> usersMap;
    private int memory_available;
    private int jobs_waiting;
    private ReentrantReadWriteLock lWRUser;
    private int clientCounter;
    
    public ServerData () throws IOException {
        lWRUser = new ReentrantReadWriteLock();
        usersMap= new HashMap<>();
        fillUserMap();
        memory_available = 101;
        jobs_waiting = 0;
        clientCounter = 0;
    }

    public void fillUserMap() throws IOException {
        lWRUser.writeLock().lock();
        try {
            File file = new File("../data/User_Data/User_data.csv");
            Scanner file_scanner = new Scanner(file);
            String current_line;
            String[] parts;
            String username;
            String password;
            while(file_scanner.hasNextLine()) {
                current_line = file_scanner.nextLine();
                parts = current_line.split(";");
                username = parts[0];
                password = parts[1];
                registerUserServer(username, password);
            }
            file_scanner.close();
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public void putUsersInDataBase() throws IOException {
        lWRUser.writeLock().lock();
        try{   
            File file = new File("../data/User_Data/User_data.csv");
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            for(Map.Entry<String, User> entry : usersMap.entrySet()) {
                User current_user =  entry.getValue();
                String username = current_user.getUsername();
                String password = current_user.getPassword();
                String resultado = username+";"+password;
                writer.write(resultado);
                writer.newLine();
            }
            writer.close();
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public Boolean registerUserServer(String username, String password) throws IOException {
        lWRUser.writeLock().lock();
        try {
            if(!usersMap.containsKey(username)) {
                User new_User = new User(username, password, 0);
                usersMap.put(username, new_User);
                return true;// return significa que registou com sucesso
            }
            else {
                return false; // return significa que não consegiu registar
            }
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public Boolean logInUserServer(String username, String password, int client_Tag) {
        lWRUser.writeLock().lock();
        try {
            User userCheck = usersMap.get(username);
            if(usersMap.containsKey(username) && userCheck.getPassword().equals(password) && userCheck.getClient_Tag() == 0 ) {
                userCheck.setClient_Tag(client_Tag);
                return true; // return significa que efetuou logIn com sucesso
            }
            else {
                return false; // return significa que aqueles dados de logIn não correspondem a um user
            }
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public boolean logOutUserServer(String username, int client_Tag) {
        lWRUser.writeLock().lock();
        try {
            User userCheck = usersMap.get(username);
            if(userCheck.getClient_Tag() == client_Tag) {
                userCheck.setClient_Tag(0);
                return true;
            }
            else {
                return false;
            }
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    //returns true if it can decrease memory
    public Boolean decrease_memory(int size) {
        lWRUser.writeLock().lock();
        int memory_check = memory_available;
        try {
            if((memory_check -= size) >= 0) {
                memory_available -= size;
                return true;
            }
            else {
                return false;
            }
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public void increase_memory(int size) {
        lWRUser.writeLock().lock();
        try {
            memory_available += size;
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public int check_memory() {
        lWRUser.writeLock().lock();
        try {
            return memory_available;
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public int jobs_waiting_increase() {
        lWRUser.writeLock().lock();
        try {
            jobs_waiting++;
            return jobs_waiting;
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public int jobs_waiting_decrease() {
        lWRUser.writeLock().lock();
        try {
            jobs_waiting--;
            return jobs_waiting;
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public int jobs_waiting_check() {
        lWRUser.writeLock().lock();
        try {
            return jobs_waiting;
        } finally {
            lWRUser.writeLock().unlock();
        }
    }

    public int clientCounter_Increase() {
        lWRUser.writeLock().lock();
        try {
            clientCounter += 4;
            return clientCounter;
        } finally {
            lWRUser.writeLock().unlock();
        }
    }
}
