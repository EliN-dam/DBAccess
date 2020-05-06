package dbaccess;

import utils.Console;
import utils.Configuration;
import java.util.Properties;

/**
 * Clase que  trabaja con una base de datos MySQL que contiene informaci�n relativa 
 * a pel�culas.
 * @author zelda
 */
public class MySQL {
    
    Database mysql;
    
    public MySQL(){
        this.load();
        this.menu();
    }
    
    /**
     * Carga los datos de configuraci�n de la base de datos desde un archivo externo.
     */
    public void load(){
        Properties config = Configuration.loadConfig("config/MySQL.cfg");
        this.mysql = new Database("jdbc:mysql://", 
               config.getProperty("url"),
               config.getProperty("port"),
               config.getProperty("db"),
               config.getProperty("login"),
               config.getProperty("password")
        );
    }
    
    /**
     * Muestra el menu con los diferentes ejercicios que usan esta clase.
     */
    public void menu(){
        byte option = 0;
        String[] mainMenu = { 
            "A�adir una pel�cula",
            "Modificar una pel�cula",
            "Eliminar una pel�cula ",
            "Mostrar listado de pel�culas",
            "Mostrar vista resumen de pel�culas"
        };
        do {
            Console.showMenu("MySQL: Lista de pel�culas", mainMenu);
            try {
                option = (byte)Console.readNumber(Console.eof + "Escoge una opci�n: ", "byte");
                System.out.println();
                switch(option) {
                    case 1:
                        /*this.mysql.query("INSERT INTO film(title, description, "
                                + "release_year, language_id, length) VALUES(?,?"
                                + ",?,?,?);", new Object[] {
                                    "Sonic la pel�cula",
                                    "Sonic es un peque�o erizo humanoide azul "
                                    + "proveniente de otra dimensi�n que puede "
                                    + "correr a velocidades supers�nicas",
                                    2020, 1, 99
                                });*/
                        this.mysql.query("INSERT INTO film(title, description, "
                                + "release_year, language_id, length) VALUES(?,?"
                                + ",?,?,?);", entryFilmValues());
                        Console.toContinue();
                        break;
                    case 2:
                        this.mysql.query("UPDATE film SET description = ? WHERE"
                                + " title = ?;", updateFilmValues());
                        Console.toContinue();
                        break;
                    case 3:
                        this.mysql.query("DELETE FROM film WHERE title = ?;", 
                                deteleFilmValues());
                        Console.toContinue();
                        break;
                    case 4:
                        this.mysql.select("SELECT film_id, title, description "
                                + "FROM film_text ORDER BY film_id DESC LIMIT 10;", 
                                "film_text");
                        Console.toContinue();
                        break;
                    case 5:
                        this.mysql.select("SELECT FID, title, description, "
                                + "category, price, length, rating FROM film_list"
                                + ";", "film_list");
                        Console.toContinue();
                        break;
                }
            } catch (Exception e) {
                System.out.println(Console.eof + "Opci�n no v�lida, intente lo de nuevo..." + Console.eof);
                option = 1;
            }
        } while (Console.inRange((int)option, 1, mainMenu.length));
    }
    
    /**
     * Solicita al usuario valores sobre una pel�cula.
     * @return Array con los valores introducidos por el usuario.
     */
    public static Object[] entryFilmValues(){
        Object[] values = new Object[5];
        values[0] = Console.readLine("Escribe el nombre de la pel�cula: ").trim();
        values[1] = Console.readLine("Escribe una descipci�n de la pel�cula: ").trim();
        values[2] = Console.validInt("Escribe el a�o de estreno: ");
        values[3] = getLanguage();
        values[4] = Console.validInt("Por �ltimo, escribe la duraci�n del metraje"
                + " (en minutos): ");
        return values;
    }
    
    /**
     * Solicita al usuario un idioma y valida que est� entre los disponibles.
     * @return Valor entero asociado al idioma introducido.
     */
    public static int getLanguage(){
        do {
            int value = getLanguageID(Console.readLine("Escribe el idioma el la "
                    + "cinta: "));
            if (value != 0)
                return value;
            else
                System.out.println("Idioma no v�lido, escoge otro (ingl�s, italiano,"
                        + " chino, japones, franc�s o alem�n).");
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
            case "ingl�s":
                return 1;
            case "italiano":
                return 2;
            case "japones":
            case "japon�s":
                return 3;
            case "chino":
            case "mandarin":
            case "mandar�n":
                return 4;
            case "frances":
            case "franc�s":
                return 5;
            case "aleman":
            case "alem�n":
                return 6;
            default:
                return 0;
        }
    }
    
    /**
     * Solicita al usuario valores para la modificaci�n de una pel�cula.
     * @return Array con los valores introducidos por el usuario.
     */
    public static Object[] updateFilmValues(){
        Object[] values = new Object[2];
        values[1] = Console.readLine("Escribe el nombre de la pel�cula a "
                                    + "modificar: ").trim();
        values[0] = Console.readLine("Escribe una nueva descipci�n para la "
                                    + "pel�cula: ").trim();
        return values;
    }
    
    /**
     * Solicita al usuario valores para eliminar de una pel�cula.
     * @return Array con los valores introducidos por el usuario.
     */
    public static Object[] deteleFilmValues(){
        Object[] values = new Object[1];
        values[0] = Console.readLine("Escribe el nombre de la pel�cula a "
                                    + "eliminar: ").trim();
        return values;
    }
}