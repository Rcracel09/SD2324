package SD2324;  

public class User {
    String username;
    String password;

    public User(String newusername, String newpassword){
        this.username=newusername;
        this.password=newpassword;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String newusername) {
        this.username=newusername;
    }

    public void setPasword(String newpassword) {
        this.username=newpassword; 
    }
}
