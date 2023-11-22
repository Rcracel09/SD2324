package SD2324; 

import java.io.IOException;
import java.util.Scanner;

public class MenuInicial {

    private static Client cliente;

    private static void menuregister() throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Username you want to be used:");
        String username = scanner.nextLine();
        System.out.println("Password you want to use:");
        String password = scanner.nextLine();
        System.out.println("Please write your passowrd again:");
        String passwordCheck = scanner.nextLine();
        if (!password.equals(passwordCheck)) {
            System.out.println("Error password doesn't match:");
            menuregister();
        }
        else {
                Boolean resultado = cliente.register(username, password);
                if(resultado) {
                    //System.out.println(resultado);
                    System.out.println("Success!! :D");
                    menu();
                }
                else {
                    System.out.println("Username already exists");
                    menuregister();
                }
        }
        scanner.close();
    }

    private static void menulogIn() throws IOException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your Username:");
        String username = scanner.nextLine();
        System.out.println("Please enter your Password:");
        String password = scanner.nextLine();

        Boolean resultado = cliente.logIn(username, password);
        if(resultado) {
            System.out.println("Success!! :D");
            menu();// provavelmente mandar para outro sitio para continuar
        }
        else {
            System.out.println("Password or Username are incorrect");
            menulogIn();
        }
        scanner.close();
    }

    private static void menu() throws IOException {
        System.out.println("1 - Sign-up");
        System.out.println("2 - Log-In");

        Scanner scanner = new Scanner(System.in);
        int escolha = scanner.nextInt();
        switch(escolha) {
            case 1: {
                menuregister();
                break;
            }
            case 2: {
                menulogIn();
                break;
            }
            default: {
                System.out.println("Not an option please select other character");
                break;
            }
        }
        scanner.close();        
    }

    public static void main(String[] args) {
        try {
            cliente = new Client();  // Inicialize a instância de Client
            menu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}