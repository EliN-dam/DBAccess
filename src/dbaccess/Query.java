package dbaccess;

/**
 * Interfaz que define como los m�todos que deben implementarse para realizar las
 * consultas INSERT, UPDATE y DELETE.
 * @author zelda
 */
public interface Query {
    Object[] entryValues();
    Object[] searchValues();
    Object[] updateValues();
    Object[] deleteValues();
}