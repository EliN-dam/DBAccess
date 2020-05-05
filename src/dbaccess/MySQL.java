package dbaccess;

import utils.Console;
import utils.Configuration;
import java.util.Properties;

/**
 * Clase que  trabaja con una base de datos MySQL que contiene información relativa 
 * a películas.
 * @author zelda
 */
public class MySQL {
    
    Properties config;
    
    public MySQL(){
        this.load();
        this.menu();
    }
    
    /**
     * Carga los datos de configuración de la base de datos desde un archivo externo.
     */
    public void load(){
        this.config = Configuration.loadConfig("config/MySQL.cfg");
    }
    
    /**
     * Muestra el menu con los diferentes ejercicios que usan esta clase.
     */
    public void menu(){
        byte option = 0;
        String[] mainMenu = { 
            "Añadir ina película a la base de datos",
            "Modificar una película de la base de datos",
            "Eliminar una película de la base de datos",
            "Mostrar listado de películas"
        };
        do {
            Console.showMenu("MySQL: Lista de películas", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opción: ", "byte");
                System.out.println();
                switch(option) {
                    case 1:

                        Console.toContinue();
                        break;
                    case 2:

                        Console.toContinue();
                        break;
                    case 3:

                        Console.toContinue();
                        break;
                    case 4:
                        Database mysql = new Database("jdbc:mysql://", 
                                this.config.getProperty("url"),
                                this.config.getProperty("port"),
                                this.config.getProperty("db"),
                                this.config.getProperty("login"),
                                this.config.getProperty("password")
                        );
                        mysql.select("SELECT FID, title, description, category, "
                                + "price, length, rating FROM film_list LIMIT 25", 
                                "film_list");
                        Console.toContinue();
                        break;
                }
            } catch (Exception e) {
                System.out.println(Console.eof + "Opción no válida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
    }
}
