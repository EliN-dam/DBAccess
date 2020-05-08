package dbaccess;

import java.util.InputMismatchException;
import utils.Console;

/**
 * Clase principal del proyecto, donse se inicia el mismo en el método main.
 * @author zelda
 */
public class Main {

    /**
     * Menú principal del ejercicio.
     */
    public static void main(String[] args) {
        byte option = 0;
        String[] mainMenu = { 
            "MySQL: Lista de películas",
            "SQL Server: Empresa informática",
            "PostgreSQL: Gestión de clientes",
            "SQLite: Gestión de empleados"
        };
        do {
            Console.showMenu("CONEXIONES A BASES DE DATOS", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opción: ", "byte");
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
                System.out.println(Console.eof + "Opción no válida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
        System.out.println("Gracias por utilizar nuestra aplicación ¡Que tengas un buen día! ");
    } 
}