
import java.net.Socket;


import Connections.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Client {

    private Socket s;
    private Demultiplexer m;
    private int personalTag;


    public Client() throws IOException {
        s = new Socket("localhost", 11200);
        m = new Demultiplexer(new TaggedConnection(s));
        m.start();
        String command = "startClient";
        m.send(personalTag, command.getBytes());
        try {
            byte[] tag_Bytes = m.receive(0);
            String Tag_String = new String(tag_Bytes);
            personalTag = Integer.parseInt(Tag_String);
            System.out.println("Este client tem esta tag " + Tag_String);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    //acabar esta função ela vai ter de receber do server a ver se autenticação
    //foi um sucesso ou não
    public Boolean register(String username, String password)throws IOException, InterruptedException{
        String send_Content = "register"+";"+username+";"+password;
        m.send(personalTag, send_Content.getBytes());

        byte[] result_data = m.receive(personalTag); //throws declaration feita InterruptedException
        String resultadoString = new String(result_data);
        if(resultadoString.compareTo("true") == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean logIn(String username, String password)throws IOException, InterruptedException{

        String send_Content = "logIn"+";"+username+";"+password;
        m.send(personalTag, send_Content.getBytes());

        byte[] result_data = m.receive(personalTag); //throws declaration feita InterruptedException
        String resultadoString = new String(result_data);
        if(resultadoString.compareTo("true") == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public String sendJob(String current_line) throws IOException, InterruptedException {
        //Envia uma linha para ser executada
        String send_Content = "JobFunction"+";"+current_line;
        m.send(personalTag, send_Content.getBytes());
        //
        byte[] result_data = m.receive(personalTag);
        String resultadoString = new String(result_data);
        String[] parts = resultadoString.split(";");
        String file_name = parts[0];
        String final_output = parts[1];
        //Trata da Exeception que ocorre em JobFunction
        if(final_output.equals("Falhou ao executar")) {
            String resultado = file_name + " " + "Falhou ao executar";
            return resultado;
        }
        //Parte do codigo que escreve o resultado no ficheiro
        else {
            String resultado = "Acabei de receber o " + file_name;
            File output = new File("../Resultados/" +personalTag+"Cliente"+file_name);
            FileWriter fw = new FileWriter(output);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(final_output);
            writer.close();
            return resultado;
            }
    }

    public String consult(String consultTitle) throws IOException, InterruptedException {
        System.out.println("Vou verificar o atual estado de ocupação do serviço");
        String send_Content = "Consult";
        m.send(personalTag + 1, send_Content.getBytes());

        byte[] result_data = m.receive(personalTag+1);
        String resultadoString =  new String(result_data);
        String[] parts = resultadoString.split(";");
        String memory = parts[0];
        String waiting = parts[1];
        String resultado =" A sua consulta " + consultTitle + " diz que a memória disponivel é " + memory + " e que tem uma fila de espera de " + waiting + " Tarefas";
        return resultado;
    }

    public Boolean logout (String username) throws IOException, InterruptedException {
        String send_content = "logOut;"+username;
        m.send(personalTag+2, send_content.getBytes());

        byte[] result_data = m.receive(personalTag+2);
        String resultadoString = new String(result_data);
        if(resultadoString.equals("true")) {
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean closeServer(String password) throws IOException, InterruptedException {
        String send_content = "ServerClose;"+password;
        m.send(personalTag+3, send_content.getBytes());

        byte[] result_data = m.receive(personalTag+3);
        String resultadoString = new String (result_data);
        if(resultadoString.equals("true")) {
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
