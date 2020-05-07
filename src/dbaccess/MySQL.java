package dbaccess;

import java.util.InputMismatchException;
import utils.Console;
import utils.Configuration;
import java.util.Properties;

/**
 * Clase que trabaja con una base de datos MySQL que contiene información relativa 
 * a películas.
 * @author zelda
 */
public class MySQL implements Query {
    
    Database mysql;
    
    public MySQL(){
        this.load();
        this.menu();
    }
    
    /**
     * Carga los datos de configuración de la base de datos desde un archivo externo.
     */
    public void load(){
        Properties config = Configuration.loadConfig("config/MySQL.cfg");
        this.mysql = new Database( 
               config.getProperty("url"),
               config.getProperty("port"),
               config.getProperty("db"),
               config.getProperty("login"),
               config.getProperty("password"),
               "MySQL"
        );
    }
    
    /**
     * Muestra el menu con los diferentes acciones que podemos hacer sobre la 
     * lista de películas.
     */
    public void menu(){
        byte option = 0;
        String[] mainMenu = { 
            "Añadir una película",
            "Modificar una película",
            "Eliminar una película ",
            "Buscar una película",
            "Mostrar últimas películas",
            "Mostrar vista resumen de películas"
        };
        do {
            Console.showMenu("MySQL: Lista de películas", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opción: ", "byte");
                System.out.println();
                switch(option) {
                    case 1:
                        this.mysql.query("INSERT INTO film(title, description, "
                                + "release_year, language_id, length) VALUES(?,?"
                                + ",?,?,?);", this.entryValues());
                        Console.toContinue();
                        break;
                    case 2:
                        this.mysql.query("UPDATE film SET description = ? WHERE"
                                + " title = ?;", this.updateValues());
                        Console.toContinue();
                        break;
                    case 3:
                        this.mysql.query("DELETE FROM film WHERE title = ?;", 
                                this.deleteValues());
                        Console.toContinue();
                        break;
                    case 4:
                        this.mysql.select("SELECT film_id, title, description "
                                + "FROM film_text Where title = ?;", "film_text",
                                this.searchValues());
                        Console.toContinue();
                        break;
                    case 5:
                        this.mysql.select("SELECT film_id, title, description "
                                + "FROM film_text ORDER BY film_id DESC LIMIT 10;", 
                                "film_text");
                        Console.toContinue();
                        break;
                    case 6:
                        this.mysql.select("SELECT FID, title, description, "
                                + "category, price, length, rating FROM film_list"
                                + ";", "film_list");
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
     * Solicita al usuario valores d una película para añadirla a la base de datos.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] entryValues(){
        Object[] values = new Object[5];
        values[0] = Console.readLine("Escribe el título de la película: ").trim();
        values[1] = Console.readLine("Escribe una descipción de la película: ").trim();
        values[2] = Console.validInt("Escribe el año de estreno: ");
        values[3] = getLanguage();
        values[4] = Console.validInt("Por último, escribe la duración del metraje"
                + " (en minutos): ");
        return values;
    }
    
    /**
     * Solicita al usuario un idioma y valida que esté entre los disponibles.
     * @return Valor entero asociado al idioma introducido.
     */
    public static int getLanguage(){
        do {
            int value = getLanguageID(Console.readLine("Escribe el idioma el la "
                    + "cinta: "));
            if (value != 0)
                return value;
            else
                System.out.println("Idioma no soportado, escoge otro (inglés, "
                        + "italiano, chino, japones, francés o alemán).");
        } while(true);
    }
    
    /**
     * Devuelve el ID del idioma seleccionamo.
     * @param language Idioma escogido.
     * @return Valor entero asociado al idioma.
     */
    public static int getLanguageID(String language){
        switch(language.toLowerCase().trim()){
            case "ingles":
            case "inglés":
                return 1;
            case "italiano":
                return 2;
            case "japones":
            case "japonés":
                return 3;
            case "chino":
            case "mandarin":
            case "mandarín":
                return 4;
            case "frances":
            case "francés":
                return 5;
            case "aleman":
            case "alemán":
                return 6;
            default:
                return 0;
        }
    }
    
    /**
     * Solicita al usuario el titulo de una película para realizar una búsqueda.
     * @return El título facilitado por el usuario.
     */
    @Override
    public Object[] searchValues(){
        Object[] values = new Object[1];
        values[0] = Console.readLine("Escribe el título de la película que desea"
                                    + " buscar: ").trim();
        return values;
    }
    
    /**
     * Solicita al usuario valores para la modificación de una película.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] updateValues(){
        Object[] values = new Object[2];
        values[1] = Console.readLine("Escribe el título de la película a "
                                    + "modificar: ").trim();
        values[0] = Console.readLine("Escribe una nueva descipción para la "
                                    + "película: ").trim();
        return values;
    }
    
    /**
     * Solicita al usuario el titulo de la película  a eliminar.
     * @return Array con los valores introducidos por el usuario.
     */
    @Override
    public Object[] deleteValues(){
        Object[] values = new Object[1];
        values[0] = Console.readLine("Escribe el título de la película a "
                                    + "eliminar: ").trim();
        return values;
    }
}