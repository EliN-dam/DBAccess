package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase con los métodos que nos permitira trabajar con un fichero externo de configuración.
 * https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html
 * https://mkyong.com/java/java-properties-file-examples/
 * @author zelda
 */
public class Configuration {
    
    /**
     * Carga las propiedades de configuración desde un archivo externo.
     * @param url Ruta del archivo de configuración.
     * @return Una colección con las propiedades de configuración.
     */
    public static Properties loadConfig(String url){
        try (InputStream file = new FileInputStream(url)){
            Properties current = new Properties();
            current.load(file);
            return current;
        } catch (IOException e){
            System.out.println("No se han podido cargar el archivo de configuración:");
            e.printStackTrace();
            return null;
        }
    }    
}
