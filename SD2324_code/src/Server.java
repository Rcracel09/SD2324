import sd23.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
                    String content = new String(frame.getData());
                    String[] parts = content.split(";");
                    String command = parts[0];
            
                    while(command!=null) {
                        switch(command) {
                            case "startClient": {
                                client_Tag = serverData.clientCounter_Increase();
                                String client_Tag_String = String.valueOf(client_Tag);
                                conn.send(0,client_Tag_String.getBytes());
                                System.out.println("Acabei de por o client na base de dados com a tag " + client_Tag_String);
                                break;
                            }
                            case "register": {
                                String username = parts[1];
                                String password = parts[2];
                                String resultado = String.valueOf(serverData.registerUserServer(username, password));
                                conn.send(client_Tag, resultado.getBytes());
                                break;
                            }
                            case "logIn": {
                                String username = parts[1];
                                String password = parts[2];
                                String resultado = String.valueOf(serverData.logInUserServer(username, password,client_Tag));
                                conn.send(client_Tag, resultado.getBytes());
                                break;
                            }
                            case "logOut": {
                                String username= parts[1];
                                String resultado = String.valueOf(serverData.logOutUserServer(username, client_Tag-2));
                                conn.send(client_Tag,resultado.getBytes());
                                break;
                            }
                            case "JobFunction" : {
                                String file_name = parts[1];
                                String bytes_being_used = parts[2];
                                int bytes_Int = Integer.parseInt(bytes_being_used);
                                serverData.jobs_waiting_increase();
                                serverLock.lock();
                                try{
                                    while(!serverData.decrease_memory(bytes_Int)) {
                                        System.out.println(file_name + "Não coube no server logo espera");
                                        cond.await();
                                        }
                                } finally {
                                    serverLock.unlock();
                                }
                                serverData.jobs_waiting_decrease();
                                String jobString = parts[3];
                                try{
                                    byte [] job_output = JobFunction.execute(jobString.getBytes());
                                    jobString = new String(job_output);
                                    serverData.increase_memory(bytes_Int);
                                    serverLock.lock();
                                    try{
                                        cond.signalAll();
                                    } finally {
                                        serverLock.unlock();
                                    }
                                    String resultado = file_name+";"+jobString;
                                    conn.send(client_Tag, resultado.getBytes());
                                } catch (JobFunctionException exception){
                                    String resultado = file_name + ";" +"Falhou ao executar";
                                    serverData.increase_memory(bytes_Int);
                                    serverLock.lock();
                                    try{
                                        cond.signalAll();
                                    } finally {
                                        serverLock.unlock();
                                    }
                                    System.out.println(file_name + " Falhou ao executar");
                                    conn.send(client_Tag,resultado.getBytes());
                                }
                                break;
                            }
                            case "Consult": {
                                int result_memory = serverData.check_memory();
                                int result_waiting = serverData.jobs_waiting_check();
                                String result_String = String.valueOf(result_memory) + ";" + String.valueOf(result_waiting);
                                conn.send(client_Tag, result_String.getBytes());
                                break;
                            }
                            case "ServerClose": {
                                String password = parts[1];
                                if(password.equals("123")){
                                    String resultado = "true";
                                    serverData.putUsersInDataBase();
                                    conn.send(client_Tag, resultado.getBytes());
                                    Thread.sleep(1000);
                                    ss.close();
                                    System.exit(0);
                                }
                                else {
                                    String resultado = "false";
                                    conn.send(client_Tag, resultado.getBytes());
                                }
                                break;
                            }

                        } 
                    frame = conn.receive();
                    client_Tag =frame.getTag();
                    content = new String(frame.getData());
                    parts = content.split(";");
                    command = parts[0];
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