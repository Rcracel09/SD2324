import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Result {
    Boolean resultado;
    int tag_or_memory;
    int waiting_or_code;
    String message;
    byte[] job;

    
    public Result(Boolean resultado, int tag_or_memory, int waiting_or_code, String message, byte[] job) {
        this.resultado =  resultado;
        this.tag_or_memory = tag_or_memory;
        this.waiting_or_code = waiting_or_code;
        this.message = message;
        this.job = job; 
    }
    
    public Result(Boolean resultado) {
        this.resultado =  resultado;
        this.tag_or_memory = 0;
        this.waiting_or_code = 0;
        this.message = "EMPTY";
        this.job = "123".getBytes(); 
    }
    
    public Result(int tag_or_memory) {
        this.resultado =  true;
        this.tag_or_memory = tag_or_memory;
        this.waiting_or_code = 0;
        this.message = "EMPTY";
        this.job = "123".getBytes(); 
    }

    public Result(int tag_or_memory, int waiting_or_code) {
        this.resultado =  true;
        this.tag_or_memory = tag_or_memory;
        this.waiting_or_code = waiting_or_code;
        this.message = "EMPTY";
        this.job = "123".getBytes(); 
    }
    
    public Result(Boolean resultado, int waiting_or_code, String message) {
        this.resultado = resultado;
        this.tag_or_memory = 0;
        this.waiting_or_code = waiting_or_code;
        this.message = message;
        this.job = "123".getBytes(); 
    }
    
    public Result(Boolean resultado, String message, byte[] job) {
        this.resultado = resultado;
        this.tag_or_memory = 0;
        this.waiting_or_code = 0;
        this.message = message;
        this.job = job; 
    }

    public Boolean get_Resultado() {
        return this.resultado;
    }

    public int get_memory() {
        return this.tag_or_memory;
    }

    public int get_Tag() {
        return this.tag_or_memory;
    }

    public int get_code() {
        return this.waiting_or_code;
    }

    public int get_Waiting() {
        return this.waiting_or_code;
    }

    public String get_Message() {
        return this.message;
    }

    public byte[] get_Job() {
        return this.job;
    }
      

    public byte[] serialize() throws IOException{

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.writeBoolean(this.resultado);
        dataOutputStream.writeInt(this.tag_or_memory);        
        dataOutputStream.writeInt(this.waiting_or_code);
        dataOutputStream.writeUTF(this.message);
        dataOutputStream.writeInt(this.job.length);
        dataOutputStream.write(this.job);
        dataOutputStream.flush();

        return byteArrayOutputStream.toByteArray();
    }


    public static Result deserialize(byte[] dataJob) throws IOException{

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataJob);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);


        Boolean resultado = dataInputStream.readBoolean();
        int tag_or_memory = dataInputStream.readInt();
        int waiting_or_code = dataInputStream.readInt();
        String message = dataInputStream.readUTF();

        int job_length = dataInputStream.readInt();
        byte[] job = new byte[job_length];
        dataInputStream.readFully(job);

        return new Result(resultado, tag_or_memory, waiting_or_code, message,job);
    }
    
}
