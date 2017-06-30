package info.mschmitt.battyboost.core.utils;

/**
 * @author Matthias Schmitt
 */
public class CharacterClasses {
    public static final char[] SYMBOL;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            tmp.append(ch);
        }
        SYMBOL = tmp.toString().toCharArray();
    }
}
