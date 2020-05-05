package dbaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

/**
 * 
 * @author zelda
 */
public class Database {
    
    private Statement current;
    private ResultSet result;
    private String query;
    private int[] sizes;
    private int nColumns;
    private ResultSetMetaData mData;
    
    public Database(String connection){
        try {
            Connection server = this.connect(
                    connection,
                    "192.168.1.108:3306/sakila",
                    "root",
                    ""
            );
            this.current = server.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE, // Permite navegar hacia atrás en el ResultSet.
                    ResultSet.CONCUR_READ_ONLY
            );
        } catch (SQLException e) {
            System.out.println("No se ha podido realizar la conexión a la base "
                    + "de datos:");
            e.printStackTrace();
        }
    }

    public String getQuery() {
        return query;
    }
    
    /**
     * Realiza una conexión a la base de datos en base a los parametros seleccionados.
     * @param access Sintaxis con el acceso a la base de datos.
     * @param url Dirección de la base datos.
     * @param user Usuario de la base de datos.
     * @param pass Contraseña del usuario de la base de datos.
     * @return El objeto creado con la conexión a la base de datos.
     * @throws SQLException 
     */
    public Connection connect(String access, String url, String user, 
            String pass) throws SQLException{
        return DriverManager.getConnection(access + url, user, pass);
    }  
    
    /**
     * Realiza una consulta de selección a la base de datos y almacena sus resultados.
     * @param query String con la consulta a realizar.
     */
    public void select(String query){
        try {
            this.result = this.current.executeQuery(this.query = query);
            this.mData = this.result.getMetaData();
        } catch (SQLException e){
            System.out.println("No se ha podido realizar la consulta: ");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra los resultado de la consulta en consola.
     */
    public void showResults(){
        try {
            if (this.loadSizes())
                this.printTable();
        } catch (SQLException e){
            System.out.println("Ha ocurrido un error al mostrar los resultados de "
                    + "la consulta: ");
            e.printStackTrace();
        }
    }
    
    /**
     * Recorre los resultados de la consulta midiendo los tamaños de cada columna 
     * y el total de la tabla necesarios para dibujar la tabla.
     * @return <ul>
     *              <li>True - Si ha conseguidos y registrado los tamaños</li>
     *              <li>False - Si no ha podido obtener los tamaños</li>
     *         </ul>
     * @throws SQLException 
     */
    public boolean loadSizes() throws SQLException{
        if (this.result != null){
            this.nColumns = this.result.getMetaData().getColumnCount();
            this.sizes = new int[this.nColumns + 1];
            int length;
            this.result.beforeFirst(); // Nos aseguramos que empieza por el principio.
            for (int i = 1; i <= this.nColumns; i++){ // Contamos la cabecera.
                length = this.mData.getColumnLabel(i).length();
                    if (this.sizes[i] < length)
                        this.sizes[i] = length;
            }
            while (this.result.next()){ // Contamos los resultados.
                for (int i = 1; i <= this.nColumns; i++){
                    length = this.result.getString(i).length();
                    if (this.sizes[i] < length)
                        this.sizes[i] = length;
                }
            }
            length = 0; // Reinicio la variable para reutilizarla.
            for (int i = 1; i < this.sizes.length; i++){
                length += this.sizes[i];
            }
            sizes[0] = length; // Almaceno la longitud total.
            return true;
        }
        return false;
    }
    
    /**
     * Dibuja una tabla en consola mostrando los resultados de la consulta.
     * @throws SQLException 
     */
    public void printTable() throws SQLException{
        int maxSize = this.sizes[0] + (this.nColumns * 2) + (this.nColumns - 1);
        String interline = "+";
        for (int i = 1; i < this.sizes.length; i++)
            interline += new String(new char[this.sizes[i]]).replace('\0', '=') 
                    + "==" + "+"; 
        this.printTitle(maxSize);
        this.printHead(interline);
        this.printTuples(maxSize, interline);
    }
    
    /**
     * Dibuja la cabecerá con el título de la tabla.
     * @param totalSize Define la longitud total de la tabla.
     */
    public void printTitle(int totalSize){
        int titleLength = this.query.length();
        /* (this.nFields * 2) -> 2 espacios adicionales a cada lado para cada dato
         * (this.nFields - 1) -> el espacio para cada columna separadora. */
        System.out.println("+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
        System.out.printf("|%" + (totalSize - titleLength) / 2 +
                          "s%" + titleLength +
                          "s%" + ((totalSize - titleLength) + 1) / 2 + "s|" +
                          System.lineSeparator(), 
                          "", this.query, "");
        System.out.println("+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
    }
    
    /**
     * Dibuja la cabecera de la tabla con los nombres de las columnas.
     * @param interline Cadena de caracteres con los bordes entre filas.
     * @throws SQLException 
     */
    public void printHead(String interline) throws SQLException{
        int length;
        this.result.beforeFirst(); // Nos aseguramos que empieza por el principio.
        System.out.print("|");
        for (int i = 1; i <= this.nColumns; i++){
            length = this.mData.getColumnLabel(i).length();
            System.out.printf("%" + (this.sizes[i] - length + 2) / 2 +
                                  "s%-" + length +
                                  "s%" + (this.sizes[i] - length + 3)  / 2 + "s|",
                                  "", mData.getColumnLabel(i) , "");
        }
        System.out.println("\n" + interline);
    }
    
    /**
     * Dibuja por consola las filas con los registros almacenados de la consulta.
     * @param totalSize Define la longitud total de las filas.
     * @param interline Cadena de caracteres con los bordes entre filas.
     * @throws SQLException 
     */
    public void printTuples(int totalSize, String interline) throws SQLException{
        int length;
        while (this.result.next()){
            System.out.print("|");
            for (int i = 1; i <= this.nColumns; i++){
                length = this.result.getString(i).length();
                System.out.printf("%" + (this.sizes[i] - length + 2) / 2 +
                                  "s%-" + length +
                                  "s%" + (this.sizes[i] - length + 3)  / 2 + "s|",
                                  "", this.result.getString(i), "");
            }
            if (!this.result.isLast())
                System.out.println("\n" + interline);
        }
        System.out.println("\n+" + new String(new char[totalSize]).replace('\0', '=') 
                + "+");
    }
}