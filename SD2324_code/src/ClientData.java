
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import Connections.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClientData {

    private Socket s;
    private Demultiplexer m;
    private int personalTag;


    public ClientData() throws IOException {
        s = new Socket("localhost", 11200);
        m = new Demultiplexer(new TaggedConnection(s));
        m.start();
        Command info = new Command("startClient");
        byte[] infoArray = info.serialize();
        m.send(0, infoArray);
        try {
            infoArray = m.receive(personalTag);
            Result content = Result.deserialize(infoArray);
            int resultadoTeste = content.get_Tag();
            System.out.println("Este client tem esta tag " + resultadoTeste);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }

    //acabar esta função ela vai ter de receber do server a ver se autenticação
    //foi um sucesso ou não
    public Boolean register(String username, String password)throws IOException, InterruptedException{
        Command info = new Command("register", username, password);
        byte[] infoArray = info.serialize();
        m.send(personalTag, infoArray);

        infoArray = m.receive(personalTag);
        Result content = Result.deserialize(infoArray);
        Boolean resultado = content.get_Resultado();
        return resultado;
    }

    public Boolean logIn(String username, String password)throws IOException, InterruptedException{
        Command info = new Command("logIn", username, password);
        byte[] infoArray = info.serialize();
        m.send(personalTag, infoArray);

        infoArray = m.receive(personalTag); //throws declaration feita InterruptedException
        Result content = Result.deserialize(infoArray);
        Boolean resultado = content.get_Resultado();
        return resultado;
    }

    public String sendJob(String current_line) throws IOException, InterruptedException {
        String[] parts = current_line.split(";");
        String file_name = parts[0];
        
        String memoryString = parts[1];
        int memory = Integer.parseInt(memoryString);
        
        String jobString = parts[2];
        byte [] job = Files.readAllBytes(Path.of(jobString));
        
        Command info = new Command("JobFunction",memory, file_name,job);
        byte[] infoArray = info.serialize();
        m.send(personalTag, infoArray);
        
        //
        infoArray = m.receive(personalTag); 
        Result content = Result.deserialize(infoArray);
        Boolean Job_sucess = content.get_Resultado();
        file_name = content.get_Message();
        //Trata da Exeception que ocorre em JobFunction
        if(!Job_sucess) {
            int errorCode = content.get_code();
            String errorMsg = content.get_Message();
            String resultado = file_name + " "+ errorMsg + " Com o código " + errorCode;
            return resultado;
        }
        //Parte do codigo que escreve o resultado no ficheiro
        else {
            String resultado = "Acabei de receber o " + file_name;
            file_name = file_name + ".7z";
            Path caminho = Paths.get("../Resultados/" + file_name);
            Files.write(caminho, content.get_Job(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return resultado;
            }
    }

    public String consult(String consultTitle) throws IOException, InterruptedException {
        Command info = new Command("Consult", consultTitle);
        byte[] infoArray = info.serialize();
        m.send(personalTag+1, infoArray);

        infoArray = m.receive(personalTag+1);
        Result content = Result.deserialize(infoArray);

        int memory = content.get_memory();
        int waiting = content.get_Waiting();
        String resultado =" A sua consulta " + consultTitle + " diz que a memória disponivel é " + memory + " e que tem uma fila de espera de " + waiting + " Tarefas";
        return resultado;
    }

    public Boolean logout(String username, String password) throws IOException, InterruptedException {
        Command info = new Command("logOut", username, password);
        byte[] infoArray = info.serialize();
        m.send(personalTag+2, infoArray);

        infoArray = m.receive(personalTag+2); 
        Result content = Result.deserialize(infoArray);
        Boolean resultado = content.get_Resultado();
        return resultado;
    }

    public Boolean closeServer(String password) throws IOException, InterruptedException {
        Command info = new Command("ServerClose", password);
        byte[] infoArray = info.serialize();
        m.send(personalTag+2, infoArray);

        infoArray = m.receive(personalTag+2);
        Result content = Result.deserialize(infoArray);
        Boolean resultado = content.get_Resultado();
        return resultado;
    }

}
