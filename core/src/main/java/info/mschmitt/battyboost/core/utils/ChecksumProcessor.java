package info.mschmitt.battyboost.core.utils;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;

/**
 * @author Matthias Schmitt
 */
public class ChecksumProcessor {
    private static final CRC32 CHECKSUM = new CRC32();

    public String appendChecksum(String string) {
        return string + checksum(string);
    }

    private char checksum(String string) {
        try {
            CHECKSUM.reset();
            CHECKSUM.update(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException();
        }
        long value = CHECKSUM.getValue();
        return CharacterClasses.SYMBOL[(int) (value % CharacterClasses.SYMBOL.length)];
    }

    public boolean check(String string) {
        return string.charAt(string.length() - 1) == checksum(string.substring(0, string.length() - 1));
    }
}
