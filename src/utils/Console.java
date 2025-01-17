package utils;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Some Static method to work with console.
 * @author zelda
 */
public class Console {
    
    /**
     * System end of file constant.
     */
    public static final String eof = System.getProperty("line.separator");
    
    /**
     * Show a message and wait util the user presses Enter to continue.
     */
    public static void toContinue() {
        System.out.println(eof + "Presiona ENTER para continuar...");
        new Scanner(System.in).nextLine();
    }
    
    /**
     * Catch a number introduced by console.
     * @param message(String) Message asking the user.
     * @param type(String) The numeric type ("byte", "short", "int", "long",
     * "float", "double").
     * @return The numeric value writed by the user. 
     * Return -1 in the case of error ocurred.
     */
    public static Number readNumber(String message, String type){
        Scanner in = new Scanner(System.in);
        System.out.print(message);
        switch (type.toLowerCase().trim()){
            case "byte":
                return in.nextByte();
            case "short":
                return in.nextShort();
            case "int":
                return in.nextInt();
            case "long":
                return in.nextLong();
            case "float":
                return in.nextFloat();
            case "double":
                return in.nextDouble();
            default:
                return -1;        
        }
    }
    
    /**
     * Ask and catch the characters introduced by console.
     * @param message(String) Message asking the user.
     * @return The string writed by the user.
     */
    public static String readLine(String message){
        Scanner in = new Scanner(System.in);
        System.out.print(message);
        return in.nextLine();
    }
    
    /**
     * Catch a single character introduced by console.
     * @param message Message asking the user.
     * @return The first character writed by the user.
     */
    public static char readCharacter(String message){
        Scanner in = new Scanner(System.in);
        System.out.print(message);
        return in.next().charAt(0);
    }
    
    /**
     * Check if a integer number is in value.
     * @param value(int) The value to be checked.
     * @param min(int) The lowest value of the range.
     * @param max(int) The highest value of the range.
     * @return True or false depends if the value is between the min 
     * and max values.
     */
    public static boolean inRange(int value, int min, int max){
        if ((value >= min) && (value <= max))
            return true;
        return false;
    }     
    
    /**
     * Show a menu in console.
     * @param title(String) Menu title.
     * @param options(String[]) Array with the options of the menu.
     */    
    public static void showMenu(String title, String[] options){
        String whiteLine = "|" + new String(new char[37]).replace('\0', ' ') + "|";
        header(title);
        for (int i = 0; i < options.length; i++)
            System.out.printf("|%-1s%2.2s.- %-41.41s%-1s|" + eof, "", i + 1, options[i], "");
        System.out.println("+======== Escribe otro numero para salir ========+");
    }
    
    /**
     * Show a box with a text in console.
     * @param title(String) Box text.
     */
    public static void header(String title){
        byte titlelength = (byte)title.length();
        System.out.println("+================================================+");
        System.out.printf("|%-" + ((48 - titlelength) / 2) + "s%" + titlelength + "s%-" + ((49 - titlelength) / 2) + 
                          "s|" + eof, "", title, "");
        System.out.println("+================================================+");
    }
    
    /**
     * Ask the user for confirmation.
     * @param message The confirmation question.
     * @return <ul>
     *             <li>True - If the user type the character 'S'</li>
     *             <li>False - If the user type the character 'N'</li> 
     *         </ul>
     */
    public static boolean makeSure(String message){
        char answer;
        boolean validChar = false;
        do {
            answer = Console.readCharacter(message + " (S/N) ");
            if (Console.validateQuestion(answer))
                validChar = true;
            else
                System.out.println("Opci�n no v�lida... ");
        } while(!validChar);
        if (Character.toLowerCase(answer) == 's')
            return true;
        else
            return false;
    }
    
    
    /**
     * Validate a returned character match with the requested value.
     * @param character The character write by the user.
     * @return <ul>
     *             <li>True - If mached</li>
     *             <li>False - If doesn't mached</li> 
     *         </ul>
     */
    public static boolean validateQuestion(char character){
        switch (Character.toLowerCase(character)){
            case 's':
            case 'n':
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Request user for a value until enter a valid integer value.
     * @param message(String) Message asking the user.
     * @return A valid integer value introduced by the user. 
     */
    public static int validInt(String message){
        do {
            try {
                int value = (int)readNumber(message, "int");
                return value;
            } catch (InputMismatchException e){
                System.out.println("Debe introducir un valor num�rico... ");
            }
        } while (true);
    }
    
    /**
     * Request user for a value until enter a valid decimal value.
     * @param message(String) Message asking the user.
     * @return A valid double value introduced by the user. 
     */
    public static double validDouble(String message){
        do {
            try {
                double value = (double)readNumber(message, "double");
                return value;
            } catch (InputMismatchException e){
                System.out.println("Debe introducir un valor num�rico v�lido... ");
            }
        } while (true);
    }
    
    /**
     * Request user for a String until enter valid String format base of its length.
     * @param message(String) Message asking the user.
     * @param maxLength Max number of characters.
     * @return A valid formated String introduced by the user. 
     */
    public static String validString(String message, int maxLength){
        do {
            String line = readLine(message).trim();
            if (inRange(line.length(), 1, maxLength))
                return line;
            else
                System.out.println("El n�mero m�ximo de caracteres es " + 
                        maxLength + "... ");
        } while (true);
    }
    
    /**
     * Ask the user for an email and check it if valid.
     * https://howtodoinjava.com/regex/java-regex-validate-email-address/
     * @param message(String) Message asking the user.
     * @return A valid email. 
     */
    public static String validEmail(String message){
        do {
            String line = readLine(message).trim();
            String regex = "^[\\w!#$%&�*+/=?`{|}~^-]+(?:\\.[\\w!#$%&�*+/=?"
                    + "`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
            if (line.matches(regex))
                return line;
            else
                System.out.println("Debes introducir una direcci�n de correo "
                        + "v�lida... ");
        } while(true);
    }
}
