
package dbaccess;

import java.util.InputMismatchException;
import utils.Console;
import utils.Configuration;
import java.util.Properties;

/**
 * Clase que trabaja con una base de datos PostgreSQL que contiene información 
 * sobre clientes de una tienda de productos multimedia.
 * @author zelda
 */
public class PostgreSQL implements Query {
    
    Database postgre;
    
    public PostgreSQL(){
        this.load();
        this.menu();
    }
    
    /**
     * Carga los datos de configuración de la base de datos desde un archivo externo.
     */
    public void load(){
        Properties config = Configuration.loadConfig("config/PostgreSQL.cfg");
        this.postgre = new Database( 
               config.getProperty("url"),
               config.getProperty("port"),
               config.getProperty("db"),
               config.getProperty("login"),
               config.getProperty("password"),
               "PostgreSQL"
        );
    }
    
    /**
     * Muestra el menu con los diferentes acciones que podemos hacer sobre la 
     * lista de clientes.
     */
    public void menu(){
        byte option = 0;
        String[] mainMenu = { 
            "Añadir un cliente a la base de datos",
            "Modificar un cliente",
            "Eliminar un cliente ",
            "Buscar un cliente",
            "Mostrar un listado de clientes",
            "Mostrar las tablas de la base de datos"
        };
        do {
            Console.showMenu("PostgreSQL: Gestión de clientes", mainMenu);
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

                        Console.toContinue();
                        break;
                    case 5:
                        /* PostgreSQL es case sensitive, para que cojas las 
                         * mayusculas hay que escribir los nombre entre comillas.
                         * https://www.postgresql.org/message-id/b7b967e00712070339j5fa60fd1uc873de03e3bd145e%40mail.gmail.com
                         */
                        this.postgre.select("SELECT \"CustomerId\", \"FirstName\""
                                + ", \"LastName\", \"Address\", \"City\", "
                                + "\"Country\", \"Phone\", \"Email\" FROM "
                                + "\"Customer\";", "Customer");
                        Console.toContinue();
                        break;
                    case 6:
                        this.postgre.select("SELECT schemaname, tablename, "
                                + "tableowner FROM pg_catalog.pg_tables WHERE "
                                + "schemaname != 'pg_catalog' AND schemaname != "
                                + "'information_schema';", "TABLAS");
                        Console.toContinue();
                        break;
                        
                }
            } catch (InputMismatchException e) {
                System.out.println(Console.eof + "Opción no válida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
    }
    
    @Override
    public Object[] entryValues(){
        Object[] values = new Object[4];
        return values;
    }
    
    @Override
    public Object[] searchValues(){
        Object[] values = new Object[4];
        return values;
    }
    
    @Override
    public Object[] updateValues(){
        Object[] values = new Object[4];
        return values;
    }
    
    @Override
    public Object[] deleteValues(){
        Object[] values = new Object[4];
        return values;
    }
}
