package SD2324; 

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

class ServerWorker implements Runnable{
    private Socket s;
    private DataOutputStream out;
    private DataInputStream in;

    private ServerData serverData;

    private ReentrantLock l = new ReentrantLock();


    public ServerWorker(Socket socket, ServerData servercurrentData) throws IOException {
        s = socket;
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
        serverData = servercurrentData;
    }

    @Override
    public void run() {
        try{
            String command = in.readUTF();

            while(command!=null) {
                switch(command) {
                    case "register": {
                        String username = in.readUTF();
                        String password = in.readUTF();
                        Boolean resultado = serverData.registerUserServer(username, password);
                        l.lock();
                        out.writeBoolean(resultado);
                        break;
                    }
                    case "logIn": {
                        String username = in.readUTF();
                        String password = in.readUTF();
                        Boolean resultado = serverData.logInUserServer(username, password);
                        l.lock();
                        out.writeBoolean(resultado);
                        break;
                    }
                }
                out.flush();
                l.unlock();
                command = in.readUTF();
            }
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

}
 public class Server { 
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(11200);
        ServerData serverData = new ServerData();

        while(true) {
            Socket s = ss.accept();
            Thread worker = new Thread(new ServerWorker(s, serverData));
            worker.start();
        }
    }
 }
