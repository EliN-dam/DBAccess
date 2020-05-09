package dbaccess;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Properties;
import utils.Configuration;
import utils.Console;

/**
 * Clase que trabajar con una base de datos local de SQLite3 que contiene una 
 * tabla con información relativa a los empleados de un negocio.
 * @author zelda
 */
public class SQLite implements Query {
    
    Database sqlite;
    
    public SQLite(){
        this.load();
        this.menu();
    }
    
    /**
     * Carga los datos de configuración de la base de datos desde un archivo externo.
     */
    public void load(){
        Properties config = Configuration.loadConfig("config/SQLite.cfg");
        this.sqlite = new Database( 
               config.getProperty("path"),
               config.getProperty("file"),
               "SQLite"
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
            "Añadir un empleado a la base de datos",
            "Cambiar la dirección de un empleado",
            "Dar de baja a un empleado",
            "Buscar un empleado",
            "Mostrar el listado actual de empleados"
        };
        do {
            Console.showMenu("SQLite: Gestión de empleados", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opción: ", "byte");
                System.out.println();
                switch(option) {
                    case 1:
                        this.sqlite.query("INSERT INTO Employees(FirstName, "
                                + "LastName, HireDate, Address, City, HomePhone)"
                                + " VALUES (?,?,?,?,?,?)", this.entryValues());
                        Console.toContinue();
                        break;
                    case 2:
                        this.sqlite.query("UPDATE Employees SET Address = ?, "
                                + "City = ? WHERE EmployeeID = ?;", 
                                this.updateValues());
                        Console.toContinue();
                        break;
                    case 3:
                        this.sqlite.query("DELETE FROM Employees WHERE "
                                + "EmployeeID = ?;", this.deleteValues());
                        Console.toContinue();
                        break;
                    case 4:
                        values = this.searchValues();
                        sizes = this.sqlite.loadSizeByQuery("SELECT "
                                + "MAX(LENGTH(EmployeeID)) as EmployeeID, "
                                + "MAX(LENGTH(FirstName)) as FirstName, "
                                + "MAX(LENGTH(LastName)) as LastName, "
                                + "MAX(LENGTH(HireDate)) as HireDate, "
                                + "MAX(LENGTH(Address)) as Address, "
                                + "MAX(LENGTH(City)) as City, "
                                + "MAX(LENGTH(HomePhone)) as HomePhone, "
                                + "COUNT(EMployeeID)"
                                + "FROM Employees WHERE FirstName = ?;", values);
                        this.sqlite.select("SELECT "
                                + "EmployeeID, "
                                + "FirstName, "
                                + "LastName, "
                                + "HireDate, "
                                + "Address, "
                                + "City, "
                                + "HomePhone "
                                + "FROM Employees WHERE FirstName = ?;", 
                                "Employees", sizes, values);
                        Console.toContinue();
                        break;
                    case 5:
                        sizes = this.sqlite.loadSizeByQuery("SELECT "
                                + "MAX(LENGTH(EmployeeID)) as EmployeeID, "
                                + "MAX(LENGTH(FirstName)) as FirstName, "
                                + "MAX(LENGTH(LastName)) as LastName, "
                                + "MAX(LENGTH(HireDate)) as HireDate, "
                                + "MAX(LENGTH(Address)) as Address, "
                                + "MAX(LENGTH(City)) as City, "
                                + "MAX(LENGTH(HomePhone)) as HomePhone, "
                                + "COUNT(EmployeeID) FROM Employees;");
                        this.sqlite.select("SELECT EmployeeID, FirstName, "
                                + "LastName, HireDate, Address, City, HomePhone "
                                + "FROM Employees;", "Employees", sizes);                        
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
     * Solicita al usuario los datos necesario para añadir un nuevo empleado.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] entryValues(){
        Object[] values = new Object[6];
        values[0] = Console.readLine("Introduce el nombre del nuevo empleado: ")
                .trim();
        values[1] = Console.readLine("Introduce los apellidos del nuevo empleado: ")
                .trim();
        // Añadimos la dechade inserción del empleado.
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        values[2] = LocalDateTime.now().format(dateFormat);
        values[3] = Console.readLine("Introduce la dirección de residencia del "
                + "empleado: ").trim();
        values[4] = Console.readLine("Introduce ahora la ciudad: ").trim();
        values[5] = Console.validString("Por último, Introduce el teléfono del "
                + "empleado: ", 15);
        return values;
    }
    
    /**
     * Solicita al usuario el nombre del empleado que desea buscar.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] searchValues(){
        Object[] values = new Object[1];
        values[0] = Console.readLine("Introduce el nombre del empleado que desea"
                + " buscar: ").trim();
        return values;
    }
    
    /**
     * Solicita al usuario el ID del empleado para modificar su dirección.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] updateValues(){
        Object[] values = new Object[3];
        values[2] = Console.validInt("Introduce el ID del empleado a modificar: ");
        values[0] = Console.readLine("Introduce la nueva dirección del empleado: ")
                .trim();
        values[1] = Console.readLine("Introduce la ciudad: ").trim();
        return values;
    }
    
    /**
     * Solicita al usuario el ID que se desea eliminar.
     * @return Array con los valores facilitador por el usuario. Devuelve NULL 
     * si el usuario no confirma la eliminación del empleado.
     */
    @Override 
    public Object[] deleteValues(){
        Object[] values = new Object[1];
        values[0] = Console.validInt("Introduce el ID del empleado a eliminar: ");
        if(Console.makeSure("Los datos almacenados se perderán ¿Está seguro?"))
            return values;
        else
            return null;
    }
}