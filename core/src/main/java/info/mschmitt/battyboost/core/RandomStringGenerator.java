package info.mschmitt.battyboost.core;

import java.util.Random;

/**
 * https://stackoverflow.com/a/41156/2317680
 *
 * @author Matthias Schmitt
 */
public class RandomStringGenerator {
    private final Random random = new Random();
    private final char[] buf;

    public RandomStringGenerator(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1: " + length);
        }
        buf = new char[length];
    }

    public String nextString() {
        for (int i = 0; i < buf.length; i++) {
            buf[i] = CharacterClasses.SYMBOL[random.nextInt(CharacterClasses.SYMBOL.length)];
        }
        return new String(buf);
    }
}
