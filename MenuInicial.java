package SD2324;  

import java.io.IOException;
import java.util.Scanner;

public class MenuInicial {


    private static void menuregister(Client client) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Username you want to be used:");
        String username = scanner.nextLine();
        System.out.println("Password you want to use:");
        String password = scanner.nextLine();
        System.out.println("Please write your passowrd again:");
        String passwordCheck = scanner.nextLine();
        if (!password.equals(passwordCheck)) {
            System.out.println("Error password doesn't match:");
            menuregister(client);
        }
        else {
                Boolean resultado = client.register(username, password);
                if(resultado) {
                    //System.out.println(resultado);
                    System.out.println("Success!! :D");
                    menuInicial(client);
                }
                else {
                    System.out.println("Username already exists");
                    menuregister(client);
                }
        }
        scanner.close();
    }

    private static void menulogIn(Client client) throws IOException, InterruptedException{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your Username:");
        String username = scanner.nextLine();
        System.out.println("Please enter your Password:");
        String password = scanner.nextLine();

        Boolean resultado = client.logIn(username, password);
        if(resultado) {
            System.out.println("Success!! :D");
            menuInicial(client);// provavelmente mandar para outro sitio para continuar
        }
        else {
            System.out.println("Password or Username are incorrect");
            menulogIn(client);
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
                System.out.println("Not an option please select other character");
                break;
            }
        }
        scanner.close();        
    }
/*
 * 
 private static void menuPrincipal() {
     
}
*/

}