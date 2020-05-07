package dbaccess;

import utils.Console;
import utils.Configuration;
import java.util.Properties;

/**
 * Clase que trabaja con una base de datos SQLServer que contiene información de
 * empresa de venta de productos informáticos.
 * @author zelda
 */
public class SQLServer implements Query {
    
    Database mssql;
    
    public SQLServer(){
        this.load();
        this.menu();
    }
    
    /**
     * Carga los datos de configuración de la base de datos desde un archivo externo.
     */
    public void load(){
        Properties config = Configuration.loadConfig("config/SQLServer.cfg");
        this.mssql = new Database( 
               config.getProperty("url"),
               config.getProperty("port"),
               config.getProperty("db"),
               config.getProperty("login"),
               config.getProperty("password"),
               "SQLServer"
        );
    }
    
    /**
     * Muestra el menu con los diferentes acciones que realiza esta clase.
     */
    public void menu(){
        byte option = 0;
        String[] mainMenu = { 
            "Añadir un componente al catálogo",
            "Modificar el precio de un componente",
            "Eliminar un componente del catálogo",
            "Buscar componente en el catálogo",
            "Mostrar catálogo de componentes"
        };
        do {
            Console.showMenu("SQL Server: Empresa informática", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opción: ", "byte");
                System.out.println();
                switch(option) {
                    case 1:
                        this.mssql.query("INSERT INTO Componente VALUES(?,?,?,?);",
                                this.entryValues());
                        Console.toContinue();
                        break;
                    case 2:
                        this.mssql.query("UPDATE Componente SET precio = ? WHERE"
                                + " clave = ?;", this.updateValues());
                        Console.toContinue();
                        break;
                    case 3:
                        this.mssql.query("DELETE FROM Componente WHERE clave = ?;",
                                this.deleteValues());
                        Console.toContinue();
                        break;
                    case 4:
                        this.mssql.select("SELECT clave, descripcion, precio, "
                                + "CodTipo FROM Componente WHERE clave = ?;", 
                                "Componente", this.searchValues());
                        Console.toContinue();
                        break;
                    case 5:
                        this.mssql.select("SELECT clave, descripcion, precio, "
                                + "CodTipo FROM Componente;", "Componente");
                        Console.toContinue();
                        break;
                }
            } catch (Exception e) {
                System.out.println(Console.eof + "Opción no válida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
    }
    
    /**
     * Solcita al usuario los datos para la inserción de un núevo artículo al 
     * catálogo.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] entryValues(){
        Object[] values = new Object[4];
        values[1] = Console.readLine("Escribe una descripción del componente: ")
                .trim();
        values[0] = Console.validString("Escribe la clave del producto: ", 20);
        values[2] = Console.validDouble("Escribe el precio del producto: ");
        values[3] = getTypes();
        return values;
    }
    
    /**
     * Permite seleccionar el tipo de componente.
     * @return Valor entero asociado al tipo de componente escogido.
     */
    public static int getTypes(){
        byte value = 0;
        String[] types = {
            "Altavoces",
            "Cables y Adaptadores",
            "Discos Duros",
            "Fuentes de alimentación",
            "Impresoras Mutifunción",
            "Memorias RAM",
            "Ordenadores premontados",
            "Portátiles",
            "Ratones",
            "Software",
            "Tablets",
            "Tarjeta gráficas",
            "Teclados",
            "Torres de PC",
            "Otros"
        };
        do {
            Console.showMenu("TIPOS DE COMPONENTES", types);
            try {
                value = (byte)Console.readNumber(Console.eof + "Escoge un tipo: ", "byte");
                System.out.println();
                switch(value) {
                    case 1:
                        return 1;
                    case 2:
                        return 9;
                    case 3:
                        return 21;
                    case 4:
                        return 29;
                    case 5:
                        return 30;
                    case 6:
                        return 38;
                    case 7:
                        return 47;
                    case 8:
                        return 53;
                    case 9:
                        return 57;
                    case 10:
                        return 63;
                    case 11:
                        return 65;
                    case 12:
                        return 69;
                    case 13:
                        return 70;
                    case 14:
                        return 15;
                    case 15:
                        return 75;
                }
            } catch (Exception e) {
                System.out.println(Console.eof + "Tipo no válido, intente lo de "
                        + "nuevo..." + Console.eof);
                value = 1;
            }
        } while (Console.inRange((int)value, 1, types.length));
        return -1;
    }
    
    /**
     * Solcita al usuario la clave del componente a buscar.
     * @return La clave del artículo que se quiere encontrar.
     */
    @Override
    public Object[] searchValues(){
        Object[] values = new Object[1];
        values[0] = Console.validString("Escribe la clave del producto que desea"
                + " buscar: ", 20);
        return values;
    }
    
    /**
     * Solicita al usuario la clave del producto a modificar y el nuevo precio.
     * @return Array con los valores necesario para la modificación.
     */
    @Override
    public Object[] updateValues(){
        Object[] values = new Object[2];
        values[1] = Console.validString("Escribe la clave del producto que desea"
                + " cambiar: ", 20);
        values[0] = Console.validDouble("Escribe el nuevo precio del artículo: ");
        return values;
    }
    
    /**
     * Solicita al usuario la clave del artículo que se desea eliminar.
     * @return Array con los valores facilitador por el usuario.
     */
    @Override
    public Object[] deleteValues(){
        Object[] values = new Object[1];
        values[0] = Console.validString("Escribe la clave del producto a eliminar:"
                + " ", 20);
        return values;
    }
}