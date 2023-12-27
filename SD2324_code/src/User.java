public class User {
    String username;
    String password;
    int client_Tag;

    public User(String new_username, String new_password, int new_client_Tag){
        this.username=new_username;
        this.password=new_password;
        this.client_Tag=new_client_Tag;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getClient_Tag() {
        return client_Tag;
    }

    public void setUsername(String newusername) {
        this.username=newusername;
    }

    public void setPasword(String newpassword) {
        this.username=newpassword; 
    }

    public void setClient_Tag(int newclientTag) {
        this.client_Tag=newclientTag;
    }
}
