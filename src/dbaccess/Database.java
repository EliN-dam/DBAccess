package dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import org.sqlite.SQLiteDataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;

/**
 * Clase que contiene una serie de metodos para conectar y trabajar con bases de datos.
 * @author zelda
 */
public class Database {
    
    private final String server;
    private int port;
    private final String db;
    private final String login;
    private final String password;
    private final String dbType;
    
    public Database(String url, String port, String dbName, String login, 
            String pass, String dbType){
        this.server = url;
        if (!port.isEmpty())
            this.port = Integer.parseInt(port);
        this.db = dbName;
        this.login = login;
        this.password = pass;
        this.dbType = dbType.toLowerCase().trim();
    }
    
    /**
     * Constructor para la base de datos locales (SQLite). 
     */
    public Database(String path, String dbFile, String dbType){
        this.server = path;
        this.db = dbFile;
        this.dbType = dbType.toLowerCase().trim();
        this.login = this.password = ""; // Inicializamos las variables.
    }
    
    /**
     * Realiza una conexión a la base de datos en base a los parametros seleccionados.
     * https://www.journaldev.com/2509/java-datasource-jdbc-datasource-example
     * https://www.programcreek.com/java-api-examples/index.php?api=com.mysql.cj.jdbc.MysqlDataSource
     * https://docs.microsoft.com/es-es/sql/connect/jdbc/connection-url-sample?view=sql-server-ver15
     * https://www.postgresql.org/docs/7.3/jdbc-datasource.html
     * https://stackoverflow.com/questions/45091981/produce-a-datasource-object-for-postgres-jdbc-programmatically
     * https://github.com/xerial/sqlite-jdbc
     * @return El objeto creado con la conexión a la base de datos.
     * @throws SQLException 
     * 
     */
    public Connection connect() throws SQLException{
        //return DriverManager.getConnection(connection, user, pass);
        switch (this.dbType){
            case "mysql":
                MysqlDataSource dsMySQL = new MysqlDataSource();
                dsMySQL.setServerName(this.server);
                if (this.port != 0)
                    dsMySQL.setPortNumber(this.port);
                dsMySQL.setDatabaseName(this.db);
                dsMySQL.setUser(this.login);
                dsMySQL.setPassword(this.password);
                return dsMySQL.getConnection();
            case "mssql":
            case "sqlserver":
                SQLServerDataSource dsSQLServer = new SQLServerDataSource();
                dsSQLServer.setServerName(this.server);
                if (this.port != 0)
                    dsSQLServer.setPortNumber(this.port);
                dsSQLServer.setDatabaseName(this.db);
                dsSQLServer.setUser(this.login);
                dsSQLServer.setPassword(this.password);
                return dsSQLServer.getConnection();
            case "postgre":
            case "postgres":
            case "postgresql":
                PGSimpleDataSource dsPG = new PGSimpleDataSource();
                dsPG.setServerNames(new String[]{this.server});
                if (this.port != 0)
                    dsPG.setPortNumbers(new int[]{this.port});
                dsPG.setDatabaseName(this.db);
                dsPG.setUser(this.login);
                dsPG.setPassword(this.password);
                return dsPG.getConnection();
            case "sqlite":
                /** setDataBaseName() no funciona!!!
                 * https://stackoverflow.com/questions/41230234/using-datasource-to-connect-to-sqlite-with-xerial-sqlite-jdbc-driver
                 * https://stackoverflow.com/questions/1525444/how-to-connect-sqlite-with-java
                 */
                SQLiteDataSource dsLite = new SQLiteDataSource();
                dsLite.setUrl(dsLite.getUrl() + this.server + this.db);
                return dsLite.getConnection();
            default:
                System.out.println(this.dbType.toUpperCase() + ": tipo no soportado.");
                return null;
        }
    }  
    
    /**
     * Realiza una consulta de selección a la base de datos, almacena y muestra 
     * sus resultados.
     * https://www.arquitecturajava.com/jdbc-prepared-statement-y-su-manejo/
     * https://www.javatpoint.com/PreparedStatement-interface
     * https://docs.oracle.com/javase/8/docs/api/java/sql/CallableStatement.html
     * @param query La consulta a realizar.
     * @param tableName Nombre de la tabla.
     * @param sizes Array con tamaños de necesarios para dibujar la tabla.
     */
    public void select(String query, String tableName, int[] sizes){
        try ( // Usando el Try-With-Paramenters.
                Connection conn = this.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result = stmt.executeQuery();
            ){
            if (sizes[sizes.length - 1] > 0)
                this.printTable(result, sizes, tableName);
            else
                System.out.println("La consulta ha devuelto 0 resultados.");
        } catch (SQLException e){
            System.out.println("No se ha podido realizar la consulta a la base"
                    + " de datos");
            //e.printStackTrace();
        }
    }
    
    /**
     * Realiza una consulta de selección a la base de datos utilizando parámetros
     * como filtros, y muestra sus resultados.
     * @param query La consulta a realizar.
     * @param tableName Nombre de la tabla.
     * @param sizes Array con tamaños de necesarios para dibujar la tabla.
     * @param values Parámetros en orden que la consulta.
     */
    public void select(String query, String tableName, int[] sizes, Object[] values){
        try (
                Connection conn = this.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
            ){
            for (int i = 0; i < values.length; i++){
                stmt.setObject(i + 1, values[i]); // +1 porque los parámetros empiezan en 1.
            }
            try (ResultSet result = stmt.executeQuery();){
                if (sizes[sizes.length - 1] > 0)
                    this.printTable(result, sizes, tableName);
                else
                    System.out.println("La consulta ha devuelto 0 resultados.");
            }
        } catch (SQLException e){
            System.out.println("No se ha podido realizar la consulta a la base"
                    + " de datos");
            //e.printStackTrace();
        }
    }
           
    /**
     * Recorre los resultados de la consulta midiendo los tamaños de cada columna 
     * y el total de la tabla necesarios para dibujar la tabla.
     * OBSOLETA (DEPRECATED) Usar loadSizesByQuery() en su lugar.
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
            int sizes[] = new int[nColumns + 2];
            int length;
            String value;
            result.beforeFirst(); // Nos aseguramos que empieza por el principio.
            for (int i = 1; i <= nColumns; i++){ // Contamos la cabecera.
                length = mData.getColumnLabel(i).length();
                    if (sizes[i] < length)
                        sizes[i] = length;
            }
            while (result.next()){ // Contamos los resultados.
                for (int i = 1; i <= nColumns; i++){
                    value = result.getString(i);
                    if (value != null)
                        length = value.length();
                    else
                        length = 4;
                    if (sizes[i] < length)
                        sizes[i] = length;   
                }
                sizes[sizes.length - 1]++; // Cuenta las filas.
            }
            length = 0; // Reinicio la variable para reutilizarla.
            for (int i = 1; i < sizes.length - 1; i++){
                length += sizes[i];
            }
            sizes[0] = length; // Almaceno la longitud total.
            return sizes;
        }
        return null;
    }
    
    /**
     * Realiza una consulta que devuelve la longitud máxima de los datos 
     * contenidos en cada columna y el número de filas.
     * @param query Consulta, para que el cálculo sea correcto la  consulta debe
     * basarse en la misma que queramos mostrar a continuación, además los alias 
     * de cada columna deben coincidir con el nombre de la columna en la tabla.
     * <pre>
     * EJEMPLO:
     *      SELECT <em>name</em>, <em>phone</em> FROM <em>customer</em>
     *      SELECT MAX(LENGTH(<em>name</em>)), MAX(LENGTH(<em>phone</em>)), COUNT(*) FROM <em>customer</em>
     * </pre>
     * @return Array con los valores de los tamaños, incluido el total y el 
     * número de filas.
     */
    public int[] loadSizeByQuery(String query) {
        try (
                Connection conn = this.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet result =  stmt.executeQuery();
            ){   
            ResultSetMetaData mData = result.getMetaData();
            int[] sizes = new int[mData.getColumnCount() + 1];
            result.next();
            for (int i = 1; i < sizes.length; i++)
                sizes[i] = result.getInt(i);
            // Contamos la cabecera. La ultima posición es el número de tuplas.
            int length = 0;
            for (int i = 1; i < sizes.length - 1; i++){
                length = mData.getColumnLabel(i).length();
                    if (sizes[i] < length)
                        sizes[i] = length;
            }
            for (int i = 1; i < sizes.length - 1; i++)
                sizes[0] += sizes[i];
            return sizes;
        } catch (SQLException e){
            System.out.println("No se ha podido cargar las longitudes de los "
                    + "datos consultados");
            //e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Realiza una consulta que devuelve la longitud máxima de los datos 
     * contenidos en cada columna y el número de filas.
     * @param query Consulta, para que el cálculo sea correcto la  consulta debe
     * basarse en la misma que queramos mostrar a continuación, además los alias 
     * de cada columna deben coincidir con el nombre de la columna en la tabla.
     * <pre>
     * EJEMPLO:
     *      SELECT <em>name</em>, <em>phone</em> FROM <em>customer</em>
     *      SELECT MAX(LENGTH(<em>name</em>)), MAX(LENGTH(<em>phone</em>)), COUNT(*) FROM <em>customer</em>
     * </pre>
     * @param values Parámetros en orden que la consulta.
     * @return Array con los valores de los tamaños, incluido el total y el 
     * número de filas.
     */
    public int[] loadSizeByQuery(String query, Object[] values) {
        try (
                Connection conn = this.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
            ){
            for (int i = 0; i < values.length; i++){
                stmt.setObject(i + 1, values[i]);
            }
            try (ResultSet result =  stmt.executeQuery()){
                ResultSetMetaData mData = result.getMetaData();
                int[] sizes = new int[mData.getColumnCount() + 1];
                result.next();
                for (int i = 1; i < sizes.length; i++)
                    sizes[i] = result.getInt(i);
                int length = 0;
                for (int i = 1; i < sizes.length - 1; i++){
                    length = mData.getColumnLabel(i).length();
                        if (sizes[i] < length)
                            sizes[i] = length;
                }
                for (int i = 1; i < sizes.length - 1; i++)
                    sizes[0] += sizes[i];
                return sizes;
            }
        } catch (SQLException e){
            System.out.println("No se ha podido cargar las longitudes de los "
                    + "datos consultados");
            //e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Dibuja una tabla en consola mostrando los resultados de la consulta.
     * @param result El resultado de la consulta.
     * @param tableName Nombre de la tabla o vista.
     * @param sizes Los tamaños de cada una de las columnas.
     * @throws SQLException 
     */
    public void printTable(ResultSet result, int[] sizes, String tableName) throws SQLException{
        int nColumns = sizes.length - 2;
        int maxSize = sizes[0] + (nColumns * 2) + (nColumns - 1);
        String interline = "+";
        for (int i = 1; i <= nColumns; i++) 
            interline += new String(new char[sizes[i]]).replace('\0', '=') 
                    + "==" + "+"; 
        this.printTitle(tableName, maxSize);
        this.printHead(sizes, result.getMetaData(), interline);
        this.printTuples(result, sizes, maxSize, interline);
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
        for (int i = 1; i <= sizes.length - 2; i++){
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
     * @param totalSize Define la longitud total de las filas.
     * @param interline Cadena de caracteres con los bordes entre filas.
     * @throws SQLException 
     */
    public void printTuples(ResultSet result, int[] sizes, int totalSize,String 
            interline) throws SQLException{
        int length;
        String value;
        int counter = 0; 
        while (result.next()){
            System.out.print("|");
            for (int i = 1; i <= sizes.length - 2; i++){
                value = result.getString(i);
                if (value != null)    
                    length = value.length();
                else
                    length = 4;
                System.out.printf("%" + (sizes[i] - length + 2) / 2 +
                                  "s%-" + length +
                                  "s%" + (sizes[i] - length + 3)  / 2 + "s|",
                                  "", value, "");
            }
            // Si no es es lá última línea...
            counter++; 
            if (counter != sizes[sizes.length - 1])
                System.out.println("\n" + interline);
        }
        System.out.println("\n+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
    }
    
    /** 
     * Realiza un consulta de inserción, actualización o eliminación en una tabla 
     * de la base de datos.
     * https://stackoverflow.com/questions/21872040/jdbc-inserting-date-values-into-mysql
     * @param query La consulta.
     * @param values Parámetros en orden que la consulta.
     */
    public void query(String query, Object[] values){
        if (values == null)
            return; // caso en el que no confirma un Delete.
        try (
                Connection conn = this.connect();
                PreparedStatement stmt = conn.prepareStatement(query);
            ){
            // stmt.getParameterMetaData().getParameterCount()
            for (int i = 0; i < values.length; i++){
                stmt.setObject(i + 1, values[i]);
            }
            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("La operación se ha realizado con éxito, " 
                                  + rows + " líneas afectadas.");
            else
                System.out.println("No se han producido cambios, " + rows + " "
                                  + "lineas afectadas.");
        } catch (SQLException e){
            System.out.println("No se ha podido realizar la operación en la "
                    + "base de datos");
            //e.printStackTrace();
        }
    }
}