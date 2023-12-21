package SD2324;  

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import SD2324.TaggedConnection.Frame;



class ServerWorker implements Runnable{
    private Socket s;
    private TaggedConnection conn;

    private ServerData serverData;


    public ServerWorker(Socket socket, ServerData servercurrentData) throws IOException {
        s = socket;
        conn = new TaggedConnection(s);
        serverData = servercurrentData;
    }

    @Override
    public void run() {
        try{
            Frame command_Frame = conn.receive();
            String command = new String(command_Frame.getData());

            while(command!=null) {
                switch(command) {
                    case "register": {
                        System.out.println("Já comecei processo de registar" + command);
                        Frame frame_username = conn.receive();
                        System.out.println("Já recebi o username");
                        String username = new String(frame_username.getData());
                        System.out.println("Transformei o username em String" + username);

                        Frame frame_password = conn.receive();
                        System.out.println("Já recebi a password");
                        String password = new String(frame_password.getData());
                        System.out.println("Transformei a password em String" + password);

                        String resultado = String.valueOf(serverData.registerUserServer(username, password));
                        System.out.println("Vou tentar enviar o resultado da register" + resultado);
                        conn.send(1, resultado.getBytes());
                        System.out.println("Já enviei o resultado da register");
                        break;
                    }
                    case "logIn": {
                        System.out.println("Já comecei processo de logIn");
                        Frame frame_username = conn.receive();
                        System.out.println("Já recebi o username");
                        String username = new String(frame_username.getData());
                        System.out.println("Transformei o username em String");

                        Frame frame_password = conn.receive();
                        System.out.println("Já recebi a password");
                        String password = new String(frame_password.getData());
                        System.out.println("Transformei a password em String");

                        String resultado = String.valueOf(serverData.logInUserServer(username, password));
                        conn.send(1, resultado.getBytes());
                        break;
                    }
                    default:{
                        System.out.println("Recebi a informação errada da send");
                        break;
                    }
                } 
                command_Frame = conn.receive();
                command = new String(command_Frame.getData());
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
