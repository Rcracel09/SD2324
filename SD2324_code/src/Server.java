import sd23.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.*;

import Connections.TaggedConnection;
import Connections.TaggedConnection.Frame;;

 public class Server {
    final static int WORKERS_PER_CONNECTION = 4;
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(11200);
        ServerData serverData = new ServerData();
        ReentrantLock serverLock = new ReentrantLock();
        Condition cond = serverLock.newCondition();

        while(true) {
            Socket s = ss.accept();
            TaggedConnection conn = new TaggedConnection(s);
        
            
            Runnable worker = () -> {
                try(conn){
                    Frame frame;
                    int client_Tag;
                    frame = conn.receive();
                    client_Tag =frame.getTag();
                    byte [] content_receive = frame.getData();
                    Command info = Command.deserialize(content_receive);
                    String command = info.get_command_name();
                    // String content = new String(frame.getData());
                    // String[] parts = content.split(";");
                    // String command = parts[0];
            
                    while(command!=null) {
                        switch(command) {
                            case "startClient": {
                                client_Tag = serverData.clientCounter_Increase();
                                //String client_Tag_String = String.valueOf(client_Tag);
                                //conn.send(0,client_Tag_String.getBytes());
                                Result content = new Result(client_Tag);
                                byte[] content_send = content.serialize();
                                conn.send(0, content_send);
                                System.out.println("Acabei de por o client na base de dados com a tag " + client_Tag);
                                break;
                            }
                            case "register": {
                                String username = info.get_username();
                                String password = info.get_password();
                                Boolean resultado = serverData.registerUserServer(username, password);
                                Result content = new Result(resultado);
                                byte[] content_send = content.serialize();
                                conn.send(client_Tag, content_send);
                                break;
                            }
                            case "logIn": {
                                String username = info.get_username();
                                String password = info.get_password();
                                Boolean resultado = serverData.logInUserServer(username, password,client_Tag);
                                Result content = new Result(resultado);
                                byte[] content_send = content.serialize();
                                conn.send(client_Tag, content_send);
                                break;
                            }
                            case "logOut": {
                                String username = info.get_username();
                                String password = info.get_password(); 
                                Boolean resultado = serverData.logOutUserServer(username, password, client_Tag);
                                Result content = new Result(resultado);
                                byte[] content_send = content.serialize();
                                conn.send(client_Tag, content_send);
                                break;
                            }
                            case "JobFunction" : {
                                int bytes_being_used = info.get_job_size();
                                serverData.jobs_waiting_increase();
                                serverLock.lock();
                                try{
                                    while(!serverData.decrease_memory(bytes_being_used)) {
                                        System.out.println("Não coube no server logo espera");
                                        cond.await();
                                    }
                                } finally {
                                    serverLock.unlock();
                                }
                                serverData.jobs_waiting_decrease();
                                try{
                                    byte [] input_Job = info.get_job();
                                    byte [] job_output = JobFunction.execute(input_Job);
                                    serverData.increase_memory(bytes_being_used);
                                    serverLock.lock();
                                    try{
                                        cond.signalAll();
                                    } finally {
                                        serverLock.unlock();
                                    }
                                    String file_name = info.get_title();
                                    Result content = new Result(true, file_name,job_output);
                                    byte[] content_send = content.serialize();
                                    conn.send(client_Tag, content_send);
                                } catch (JobFunctionException exception){
                                    int code = exception.getCode();
                                    String message = exception.getMessage();
                                    serverData.increase_memory(bytes_being_used);
                                    serverLock.lock();
                                    try{
                                        cond.signalAll();
                                    } finally {
                                        serverLock.unlock();
                                    }
                                    System.out.println("Falhou ao executar");
                                    Result content = new Result(false, code, message);
                                    byte[] content_send = content.serialize();
                                    conn.send(client_Tag, content_send);
                                }
                                break;
                            }
                            case "Consult": {
                                int memory = serverData.check_memory();
                                int waiting = serverData.jobs_waiting_check();
                                Result content = new Result(memory, waiting);
                                byte[] content_send = content.serialize();
                                conn.send(client_Tag, content_send);
                                break;
                            }
                            case "ServerClose": {
                                String password = info.get_password();
                                Boolean resultado = serverData.serverclose(password);
                                Result content = new Result(resultado);
                                byte[] content_send = content.serialize();
                                if(resultado){
                                    System.out.println("A fechar...");
                                    serverData.putUsersInDataBase();
                                    conn.send(client_Tag, content_send);
                                    ss.close();
                                    System.exit(0);
                                }
                                else {
                                    conn.send(client_Tag, content_send);
                                }
                                break;
                            }
                            

                        } 
                    frame = conn.receive();
                    client_Tag =frame.getTag();
                    content_receive = frame.getData();
                    info = Command.deserialize(content_receive);
                    command = info.get_command_name();
                    }
                }
                catch(Exception e) {
                    System.out.println(e);
                }
            };

            for (int i = 0; i < WORKERS_PER_CONNECTION; ++i){
                new Thread(worker).start();
            }
        }
    }
 }