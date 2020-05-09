
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
        int[] sizes;
        Object[] values;
        String[] mainMenu = { 
            "Añadir un cliente a la base de datos",
            "Actualizar los contactos de un cliente",
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
                        this.postgre.query("INSERT INTO \"Customer\"("
                                + "\"CustomerId\", "
                                + "\"FirstName\", "
                                + "\"LastName\", "
                                + "\"Address\", "
                                + "\"City\", "
                                + "\"Country\", "
                                + "\"Phone\", "
                                + "\"Email\") "
                                + "VALUES (?,?,?,?,?,?,?,?)", this.entryValues());
                        Console.toContinue();
                        break;
                    case 2:
                        this.postgre.query("UPDATE \"Customer\" "
                                + "SET \"Phone\" = ?,\"Email\" = ? "
                                + "WHERE \"FirstName\" = ? AND \"LastName\" = ?;",
                                this.updateValues());
                        Console.toContinue();
                        break;
                    case 3:
                        this.postgre.query("DELETE FROM \"Customer\" WHERE "
                                + "\"CustomerId\" = ?;", this.deleteValues());
                        Console.toContinue();
                        break;
                    case 4:
                        values = this.searchValues();
                        sizes = this.postgre.loadSizeByQuery("SELECT "
                                + "MAX(LENGTH(\"CustomerId\"::varchar(5))) as CustomerId, "
                                + "MAX(LENGTH(\"FirstName\")) as FirstName, "
                                + "MAX(LENGTH(\"LastName\")) as LastName, "
                                + "MAX(LENGTH(\"Address\")) as Address, "
                                + "MAX(LENGTH(\"City\")) as City, "
                                + "MAX(LENGTH(\"Country\")) as Country, "
                                + "MAX(LENGTH(\"Phone\")) as Phone, "
                                + "MAX(LENGTH(\"Email\")) as Email, "
                                + "COUNT(\"CustomerId\") FROM \"Customer\" "
                                + "WHERE \"FirstName\" = ? AND \"LastName\" = ?;",
                                values);
                        this.postgre.select("SELECT "
                                + "\"CustomerId\", "
                                + "\"FirstName\", "
                                + "\"LastName\", "
                                + "\"Address\", "
                                + "\"City\", "
                                + "\"Country\", "
                                + "\"Phone\", "
                                + "\"Email\" FROM \"Customer\" WHERE "
                                + "\"FirstName\" = ? AND \"LastName\" = ?;", 
                                "Customer", sizes, values);
                        Console.toContinue();
                        break;
                    case 5:
                        /* PostgreSQL es case sensitive, para que cojas las 
                         * mayusculas hay que escribir los nombre entre comillas.
                         * https://www.postgresql.org/message-id/b7b967e00712070339j5fa60fd1uc873de03e3bd145e%40mail.gmail.com
                         */
                        sizes = this.postgre.loadSizeByQuery("SELECT "
                                + "MAX(LENGTH(\"CustomerId\"::varchar(5))) as CustomerID, "
                                + "MAX(LENGTH(\"FirstName\")) as FirstName, "
                                + "MAX(LENGTH(\"LastName\")) as LastName, "
                                + "MAX(LENGTH(\"Address\")) as Address, "
                                + "MAX(LENGTH(\"City\")) as City, "
                                + "MAX(LENGTH(\"Country\")) as Country, "
                                + "MAX(LENGTH(\"Phone\")) as Phone, "
                                + "MAX(LENGTH(\"Email\"))as Email, "
                                + "COUNT(\"CustomerId\") FROM \"Customer\";");
                        this.postgre.select("SELECT \"CustomerId\", "
                                + "\"FirstName\", "
                                + "\"LastName\", "
                                + "\"Address\", "
                                + "\"City\", "
                                + "\"Country\", "
                                + "\"Phone\", "
                                + "\"Email\" "
                                + "FROM \"Customer\";", "Customer", sizes);
                        Console.toContinue();
                        break;
                    case 6:
                        sizes = this.postgre.loadSizeByQuery("SELECT "
                                + "MAX(LENGTH(schemaname)) as schemaname, "
                                + "MAX(LENGTH(tablename)) as tablename, "
                                + "MAX(LENGTH(tableowner)) as tableowner, "
                                + "COUNT(*) FROM pg_catalog.pg_tables "
                                + "WHERE schemaname != 'pg_catalog' "
                                + "AND schemaname != 'information_schema';");
                        this.postgre.select("SELECT schemaname, tablename, "
                                + "tableowner FROM pg_catalog.pg_tables WHERE "
                                + "schemaname != 'pg_catalog' AND schemaname != "
                                + "'information_schema';", "TABLAS", sizes);
                        Console.toContinue();
                        break;     
                }
            } catch (InputMismatchException e) {
                System.out.println(Console.eof + "Opción no válida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
    }
    
    /**
     * Solicita al usuario los datos del cliente que se desea añadir.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] entryValues(){
        Object[] values = new Object[8];
        values[0] = Console.validInt("Escribe el ID del nuevo cliente: ");
        values[1] = Console.validString("Escribe el nombre del cliente: ", 40);
        values[2] = Console.validString("Escribe los apellidos del cliente: ", 20);
        values[3] = Console.validString("Introduce la dirección del cliente: ", 70);
        values[4] = Console.validString("Introduce la ciudad: ", 40);
        values[5] = Console.validString("Introduce el país: ", 40);
        values[6] = Console.validString("Introduce el teléfono del cliente: ", 24);
        values[7] = Console.validEmail("Introduce el email de contacto: ");
        return values;
    }
    
    /**
     * Solicita al usuario los nombre y apellidos del cliente sobre el que se 
     * desea realizar la búsqueda.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] searchValues(){
        Object[] values = new Object[2];
        values[0] = Console.readLine("Escribe el nombre del cliente: ").trim();
        values[1] = Console.readLine("Escribe los apellidos del cliente: ").trim();
        return values;
    }
    
    /**
     * Solicita al usuario los nombres y apellidos del cliente y sus formas de 
     * contacto.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] updateValues(){
        Object[] values = new Object[4];
        values[2] = Console.readLine("Escribe el nombre del cliente: ").trim();
        values[3] = Console.readLine("Escribe los apellidos del cliente: ").trim();
        values[0] = Console.validString("Introduce el nuevo teléfono: ", 24);
        values[1] = Console.validEmail("Introduce el nuevo email de contacto: ");
        return values;
    }
    
    /**
     * Solicita al usuario los nombre y apellidos del cliente que se desea eliminar.
     * @return Array con los valores facilitador por el usuario. Devuelve NULL 
     * si el usuario no confirma la eliminación del cliente.
     */
    @Override
    public Object[] deleteValues(){
        Object[] values = new Object[1];
        values[0] = Console.validInt("Escribe el ID del cliente a eliminar: ");
        if(Console.makeSure("Los datos almacenados se perderán ¿Está seguro?"))
            return values;
        else
            return null;
    }
}