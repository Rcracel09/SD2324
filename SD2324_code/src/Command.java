import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Command {
    String command_name;
    String username;
    String password_or_title;
    int job_size;
    byte [] job;

    public Command(String command_name, String username, String password_or_title, int job_size, byte[] job) {
        this.command_name = command_name;
        this.username = username;
        this.password_or_title = password_or_title;
        this.job_size = job_size;
        this.job = job;
    }

    public Command(String command_name, String username, String password_or_title) {
        this.command_name = command_name;
        this.username = username;
        this.password_or_title = password_or_title;
        this.job_size = 0;
        this.job = "123".getBytes();
    }

    public Command(String command_name, int job_size, String title ,byte[] job) {
        this.command_name = command_name;
        this.username = "EMPTY";
        this.password_or_title = title;
        this.job_size = job_size;
        this.job = job;
    }

    public Command(String command_name) {
        this.command_name = command_name;
        this.username = "EMPTY";
        this.password_or_title = "EMPTY";
        this.job_size = 0;
        this.job = "123".getBytes();
    }

    public Command(String command_name, String password_or_title) {
        this.command_name = command_name;
        this.username = "EMPTY";
        this.password_or_title = password_or_title;
        this.job_size = 0;
        this.job = "123".getBytes();
    }

    public String get_command_name() {
        return this.command_name;
    }

    public String get_username() {
        return this.username;
    }

    public String get_title() {
        return this.password_or_title;
    }

    public String get_password() {
        return this.password_or_title;
    }
    
    public int get_job_size() {
        return this.job_size;
    }

    public byte[] get_job() {
        return this.job;
    }

    public byte[] serialize() throws IOException{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeUTF(this.command_name);
        dataOutputStream.writeUTF(this.username);
        dataOutputStream.writeUTF(this.password_or_title);
        dataOutputStream.writeInt(this.job_size);
        dataOutputStream.writeInt(this.job.length);
        dataOutputStream.write(this.job);
        dataOutputStream.flush();

        return byteArrayOutputStream.toByteArray();
    }


    public static Command deserialize(byte[] dataJob) throws IOException{

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataJob);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        String command_name = dataInputStream.readUTF();
        String username =  dataInputStream.readUTF();
        String password_or_title = dataInputStream.readUTF();
        int job_size = dataInputStream.readInt();

        int job_length = dataInputStream.readInt();
        byte[] job = new byte[job_length];
        dataInputStream.readFully(job);

        return new Command(command_name, username, password_or_title, job_size, job);
    }

}
