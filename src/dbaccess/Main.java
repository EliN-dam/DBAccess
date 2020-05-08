package dbaccess;

import java.util.InputMismatchException;
import utils.Console;

/**
 * Clase principal del proyecto, donse se inicia el mismo en el m�todo main.
 * @author zelda
 */
public class Main {

    /**
     * Men� principal del ejercicio.
     */
    public static void main(String[] args) {
        byte option = 0;
        String[] mainMenu = { 
            "MySQL: Lista de pel�culas",
            "SQL Server: Empresa inform�tica",
            "PostgreSQL: Gesti�n de clientes",
            "SQLite: Gesti�n de empleados"
        };
        do {
            Console.showMenu("CONEXIONES A BASES DE DATOS", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opci�n: ", "byte");
                System.out.println();
                switch(option) {
                    case 1:
                        new MySQL();
                        break;
                    case 2:
                        new SQLServer();
                        break;
                    case 3:
                        new PostgreSQL();
                        break;
                    case 4:
                        new SQLite();
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println(Console.eof + "Opci�n no v�lida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
        System.out.println("Gracias por utilizar nuestra aplicaci�n �Que tengas un buen d�a! ");
    } 
}