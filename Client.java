package SD2324; 

import java.net.Socket;
import java.io.IOException;

public class Client {

    private Socket s;
    private Demultiplexer m; 

    public Client() throws IOException {
        s = new Socket("localhost", 11200);
        m = new Demultiplexer(new TaggedConnection(s));
        m.start();
    }

    //acabar esta função ela vai ter de receber do server a ver se autenticação
    //foi um sucesso ou não
    public Boolean register(String username, String password)throws IOException, InterruptedException{

        m.send(1, "register".getBytes());
        m.send(1, username.getBytes());
        m.send(1, password.getBytes());

        byte[] data = m.receive(1); //throws declaration feita InterruptedException
        String resultadoString = new String(data);
        System.out.println(resultadoString);
        if(resultadoString.compareTo("true") == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean logIn(String username, String password)throws IOException, InterruptedException{

        m.send(1, "logIn".getBytes());
        m.send(1, username.getBytes());
        m.send(1, password.getBytes());

        byte[] data = m.receive(1); //throws declaration feita InterruptedException
        String resultadoString = new String(data);
        if(resultadoString.compareTo("true") == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            Client client = new Client();  // Inicialize a instância de Client
            MenuInicial.menuInicial(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
