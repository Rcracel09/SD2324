package SD2324;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;

public class Client {

    private Socket s;

    private DataInputStream in;
    private DataOutputStream out;

    public Client() throws IOException {
        s = new Socket("localhost", 11200);
        in = new DataInputStream(s.getInputStream());
        out = new DataOutputStream(s.getOutputStream());
    }

    //acabar esta função ela vai ter de receber do server a ver se autenticação
    //foi um sucesso ou não
    public Boolean register(String username, String password)throws IOException{
        out.writeUTF("register");
        out.writeUTF(username);
        out.writeUTF(password);
        out.flush();//perguntar melhor sobre isto

        Boolean resultado = in.readBoolean(); 
        return resultado;
    }

    public Boolean logIn(String username, String password)throws IOException{
        out.writeUTF("logIn");
        out.writeUTF(username);
        out.writeUTF(password);
        out.flush();//perguntar melhor sobre isto

        Boolean resultado = in.readBoolean();
        return resultado;
    }
}
