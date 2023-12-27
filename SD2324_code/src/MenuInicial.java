import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class MenuInicial {


    private static void menuregister(Client client) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Username:");
        String username = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();
        System.out.println("Por favor escreva a sua password outra vez:");
        String passwordCheck = scanner.nextLine();
        if (!password.equals(passwordCheck)) {
            System.out.println("As passoword não correspondem tente outra vez:");
            menuregister(client);
        }
        else {
                Boolean resultado = client.register(username, password);
                if(resultado) {
                    System.out.println("Successo!! :D");
                    menuInicial(client);
                }
                else {
                    System.out.println("Username already exists");
                    menuInicial(client);
                }
        }
        scanner.close();
    }

    private static void menulogIn(Client client) throws IOException, InterruptedException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Username:");
        String username = scanner.nextLine();
        System.out.println("Password:");
        String password = scanner.nextLine();

        Boolean resultado = client.logIn(username, password);
        if(resultado) {
            System.out.println("Successo!! :D");
            menuPrincipal(client);
        }
        else {
            System.out.println("Este utilizador já se encontra logado ou as credenciais estão erradas");
            menuInicial(client);
        }
        scanner.close();
    }

    public static void menuInicial(Client client) throws IOException, InterruptedException {
        System.out.println("1 - Sign-up");
        System.out.println("2 - Log-In");

        Scanner scanner = new Scanner(System.in);
        int escolha = scanner.nextInt();
        switch(escolha) {
            case 1: {
                menuregister(client);
                break;
            }
            case 2: {
                menulogIn(client);
                break;
            }
            default: {
                System.out.println("Por favor selecione uma opção válida");
                menuInicial(client);
                break;
            }
        }
        scanner.close();        
    }

    public static void menuJob(Client client) throws InterruptedException, IOException {
        try {
            System.out.println("Por favor diga um ficheiro que queira executar");
            Scanner scanner = new Scanner(System.in);
            String file_name = scanner.nextLine();
            
            File file = new File("../data/Tests/" + file_name+".csv");
            Scanner file_scanner =  new Scanner(file);
            Scanner file_scanner_checker = new Scanner(file);
            ReentrantLock lock = new ReentrantLock();

            List<Thread> threads = new ArrayList<>();
            
            while(file_scanner_checker.hasNextLine()) { // vai mandar todas as linhas uma a uma para executar as tarefas que pede 
                file_scanner_checker.nextLine();
                Runnable worker = () -> {
                    String current_line;
                    lock.lock();
                    try {
                        current_line = file_scanner.nextLine();
                    } finally {
                        lock.unlock();
                    }
                    try {
                        String resultado = client.sendJob(current_line);
                        System.out.println(resultado);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                };

                Thread thread = new Thread(worker);
                threads.add(thread);
                thread.start();
            }
            file_scanner_checker.close();
            menuPrincipal(client);
            scanner.close();
            
            for (Thread thread : threads) {
                thread.join();
            }
            file_scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro não existe!!");
            menuPrincipal(client);
        }
    }

    private static void menuConsult(Client client) throws IOException, InterruptedException {
        System.out.println("Por favor dê um nome à sua consulta");
        Scanner scanner = new Scanner(System.in);
        String consult_name = scanner.nextLine();
        
        Runnable worker = () -> {
            String resultado;
            try {
                resultado = client.consult(consult_name);
                System.out.println(resultado);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(worker);
        thread.start();
        
        menuPrincipal(client);
        scanner.close();
    }

    private static void menuLogout(Client client) throws IOException, InterruptedException {
        System.out.println("Por favor indique o seu nome de utilizador para dar Logout");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();

        Boolean  resultado = client.logout(username);
        if (resultado) {
            System.out.println("Foi realizado Logout com sucesso");
            menuInicial(client);
        }
        else {
            System.out.println("Nome de utilizador incorreto Logout não foi realizado");
            menuPrincipal(client);            
        }
        scanner.close();
    }

    private static void menuCloseServer(Client client) throws IOException, InterruptedException {
        System.out.println("Password:");
        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();

        Boolean resultado = client.closeServer(password);
        if(resultado) {
            System.out.println("O server vai fechar");
            System.exit(0);
        }
        else {
            System.out.println("Password Errada");
            menuPrincipal(client);
        }
        scanner.close();
    }


     private static void menuPrincipal(Client client) throws IOException, InterruptedException {
        System.out.println("1 - Executar uma tarefa");
        System.out.println("2 - Verificar o estado atual de ocupação do serviço");
        System.out.println("3 - Logout");
        System.out.println("4 - Fechar o Servidor");

        Scanner scanner = new Scanner(System.in);
        int escolha = scanner.nextInt();

        switch(escolha) {
            case 1: {
                menuJob(client);
                break;
            }

            case 2: {
                menuConsult(client);
                break;
            }
            case 3: {
                menuLogout(client);
                break;
            }
            case 4: {
                menuCloseServer(client);
            }
            default: {
                System.out.println("Por favor selecione uma opção válida");
                menuInicial(client);
                break;
            }
        }
        scanner.close();
    }


}