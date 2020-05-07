package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Clase con los m�todos que nos permitira trabajar con un fichero externo de 
 * configuraci�n.
 * https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html
 * https://mkyong.com/java/java-properties-file-examples/
 * @author zelda
 */
public class Configuration {
    
    /** 
     * Carga las propiedades de configuraci�n desde un archivo externo.
     * @param url Ruta del archivo de configuraci�n.
     * @return Una colecci�n con las propiedades de configuraci�n.
     */
    public static Properties loadConfig(String url){
        try (
                InputStream is = new FileInputStream(url);
                /* https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html#load-java.io.InputStream-
                 * FileInputStream asume que el encoding es ISO-8859-1 */
                InputStreamReader file = new InputStreamReader(is, "UTF-8");
            ){
            Properties current = new Properties();
            current.load(file);
            return current;
        } catch (IOException e){
            System.out.println("No se han podido cargar el archivo de configuraci�n:");
            e.printStackTrace();
            return null;
        }
    }    
}