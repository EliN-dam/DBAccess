package dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;

/**
 * Clase que contiene una serie de metodos para conectar y trabajar con bases de datos.
 * @author zelda
 */
public class Database {
    
    private String connectionString;
    private String login;
    private String password;
    
    private String query;
    
    public Database(String connection, String url, String port, String dbName, 
            String login, String pass){
        if (port.isEmpty())
            this.connectionString = connection + url + "/" + dbName;
        else
            this.connectionString = connection + url + ":" + port + "/" + dbName;
        this.login = login;
        this.password = pass;
    }

    public String getQuery() {
        return query;
    }
    
    /**
     * Realiza una conexión a la base de datos en base a los parametros seleccionados.
     * @param connection String con el SGBD, la dirección, el puerto, y la base de datos.
     * @param user Usuario de la base de datos.
     * @param pass Contraseña del usuario de la base de datos.
     * @return El objeto creado con la conexión a la base de datos.
     * @throws SQLException 
     */

    public Connection connect(String connection, String user, String pass) 
            throws SQLException{
        return DriverManager.getConnection(connection, user, pass);
    }  
    
    /**
     * Realiza una consulta de selección a la base de datos, almacena y muestra 
     * sus resultados.
     * https://www.arquitecturajava.com/jdbc-prepared-statement-y-su-manejo/
     * https://www.javatpoint.com/PreparedStatement-interface
     * @param query La consulta a realizar.
     * @param tableName Nombre de la tabla.
     */
    public void select(String query, String tableName){
        try ( // Usando el Try-With-Paramenters.
                Connection conn = this.connect(this.connectionString, this.login, 
                        this.password);
                PreparedStatement stmt = conn.prepareStatement(
                    query,
                    ResultSet.TYPE_SCROLL_SENSITIVE, // Permite navegar hacia atrás en el ResultSet.
                    ResultSet.CONCUR_READ_ONLY
                );
                ResultSet result = stmt.executeQuery();
            ){
            this.printTable(result, this.loadSizes(result), tableName);
        } catch (SQLException e){
            System.out.println("\nNo realizar la consulta a la base de datos: ");
            e.printStackTrace();
        }
    }
    
    /**
     * Recorre los resultados de la consulta midiendo los tamaños de cada columna 
     * y el total de la tabla necesarios para dibujar la tabla.
     * @param result El resultado de la consulta realizada.
     * @return <ul>
     *              <li>True - Si ha conseguidos y registrado los tamaños</li>
     *              <li>False - Si no ha podido obtener los tamaños</li>
     *         </ul>
     * @throws SQLException 
     */
    public int[] loadSizes(ResultSet result) throws SQLException{
        if (result != null){
            ResultSetMetaData mData = result.getMetaData();
            int nColumns = mData.getColumnCount();
            int sizes[] = new int[nColumns + 1];
            int length;
            result.beforeFirst(); // Nos aseguramos que empieza por el principio.
            for (int i = 1; i <= nColumns; i++){ // Contamos la cabecera.
                length = mData.getColumnLabel(i).length();
                    if (sizes[i] < length)
                        sizes[i] = length;
            }
            while (result.next()){ // Contamos los resultados.
                for (int i = 1; i <= nColumns; i++){
                    length = result.getString(i).length();
                    if (sizes[i] < length)
                        sizes[i] = length;
                }
            }
            length = 0; // Reinicio la variable para reutilizarla.
            for (int i = 1; i < sizes.length; i++){
                length += sizes[i];
            }
            sizes[0] = length; // Almaceno la longitud total.
            return sizes;
        }
        return null;
    }
    
    /**
     * Dibuja una tabla en consola mostrando los resultados de la consulta.
     * @param result El resultado de la consulta.
     * @param tableName Nombre de la tabla o vista.
     * @param sizes Los tamaños de cada una de las columnas.
     * @throws SQLException 
     */
    public void printTable(ResultSet result, int[] sizes, String tableName) throws SQLException{
        ResultSetMetaData mData = result.getMetaData();
        int nColumns = mData.getColumnCount();
        int maxSize = sizes[0] + (nColumns * 2) + (nColumns - 1);
        String interline = "+";
        for (int i = 1; i < sizes.length; i++)
            interline += new String(new char[sizes[i]]).replace('\0', '=') 
                    + "==" + "+"; 
        this.printTitle(tableName, maxSize);
        this.printHead(sizes, mData, interline);
        this.printTuples(result, sizes, nColumns, maxSize, interline);
    }
    
    /**
     * Dibuja la cabecera con el título de la tabla.
     * @param tableName Nombre de la tabla o vista.
     * @param totalSize Define la longitud total de la tabla.
     */
    public void printTitle(String tableName, int totalSize){
        int titleLength = tableName.length();
        /* (this.nFields * 2) -> 2 espacios adicionales a cada lado para cada dato
         * (this.nFields - 1) -> el espacio para cada columna separadora. */
        System.out.println("+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
        System.out.printf("|%" + (totalSize - titleLength) / 2 +
                          "s%" + titleLength +
                          "s%" + ((totalSize - titleLength) + 1) / 2 + "s|" +
                          System.lineSeparator(), 
                          "", tableName.toUpperCase(), "");
        System.out.println("+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
    }
    
    /**
     * Dibuja la cabecera de la tabla con los nombres de las columnas.
     * @param sizes Los tamaños de cada una de las columnas.
     * @param data Datos de la consulta como los nombres de las columnas o su número.
     * @param interline Cadena de caracteres con los bordes entre filas.
     * @throws SQLException 
     */
    public void printHead(int[] sizes, ResultSetMetaData data, String interline) 
            throws SQLException{
        int length;
        System.out.print("|");
        for (int i = 1; i <= data.getColumnCount(); i++){
            length = data.getColumnLabel(i).length();
            System.out.printf("%" + (sizes[i] - length + 2) / 2 +
                                  "s%-" + length +
                                  "s%" + (sizes[i] - length + 3)  / 2 + "s|",
                                  "", data.getColumnLabel(i) , "");
        }
        System.out.println("\n" + interline);
    }
    
    /**
     * Dibuja por consola las filas con los registros almacenados de la consulta.
     * @param result El resultado de la consulta.
     * @param sizes Los tamaños de cada una de las columnas.
     * @param nColumns Número de columnas que ha devuelto la consulta.
     * @param totalSize Define la longitud total de las filas.
     * @param interline Cadena de caracteres con los bordes entre filas.
     * @throws SQLException 
     */
    public void printTuples(ResultSet result, int[] sizes, int nColumns, int totalSize,
            String interline) throws SQLException{
        int length;
        result.beforeFirst(); // Nos aseguramos que empieza por el principio.
        while (result.next()){
            System.out.print("|");
            for (int i = 1; i <= nColumns; i++){
                length = result.getString(i).length();
                System.out.printf("%" + (sizes[i] - length + 2) / 2 +
                                  "s%-" + length +
                                  "s%" + (sizes[i] - length + 3)  / 2 + "s|",
                                  "", result.getString(i), "");
            }
            if (!result.isLast())
                System.out.println("\n" + interline);
        }
        System.out.println("\n+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
    }
    
    /**
     * Realiza un consulta de inserción en una tabla de la base de datos.
     * @param query La consulta.
     * @param value1 Valores a insertar en el mismo orden que la consulta.
     * @param value2 Valores a insertar en el mismo orden que la consulta.
     * @param value3 Valores a insertar en el mismo orden que la consulta.
     * @param value4 Valores a insertar en el mismo orden que la consulta.
     * @param value5 Valores a insertar en el mismo orden que la consulta.
     */
    public void insert(String query, String value1, String value2 , int value3, 
            int value4, int value5){
        try (
                Connection conn = this.connect(this.connectionString, this.login, 
                        this.password);
                PreparedStatement stmt = conn.prepareStatement(
                    query,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
                );
        ){
            stmt.setString(1, value1);
            stmt.setString(2, value2);
            stmt.setObject(3, value3); // SetObject por el dato es de tipo Year.
            stmt.setInt(4, value4);
            stmt.setInt(5, value5);
            int rows = stmt.executeUpdate();
            System.out.println("La inserción se ha realizado con éxito, " + rows
                               + " líneas afectadas.");
        } catch (SQLException e){
            System.out.println("\nNo se ha podido realizar la inserción en la "
                    + "base de datos: ");
            e.printStackTrace();
        }
    }
}