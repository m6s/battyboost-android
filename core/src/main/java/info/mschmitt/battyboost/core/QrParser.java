package info.mschmitt.battyboost.core;

import info.mschmitt.battyboost.core.utils.ChecksumProcessor;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class QrParser {
    public static final int USER_QR_LENGTH = 13;
    public static final int BATTERY_QR_LENGTH = 12;
    private static final String PREFIX = "https://battyboost.com/qr?";
    private final ChecksumProcessor checksumProcessor = new ChecksumProcessor();

    public ParsingResult parseUrl(String url, Target target) {
        if (url == null) {
            return new ParsingResult(ValidationError.INVALID);
        }
        if (!url.startsWith(PREFIX)) {
            return new ParsingResult(ValidationError.INVALID);
        }
        String text = url.substring(PREFIX.length());
        return parse(text, target);
    }

    public ParsingResult parse(String text, Target target) {
        if (text == null) {
            return new ParsingResult(ValidationError.INVALID);
        }
        int length = text.length();
        if (target == Target.BATTERY && length != BATTERY_QR_LENGTH) {
            return new ParsingResult(ValidationError.INVALID);
        }
        if (target == Target.USER && length != USER_QR_LENGTH) {
            return new ParsingResult(ValidationError.INVALID);
        }
        char version = text.charAt(0);
        if (version != '0') {
            return new ParsingResult(ValidationError.INVALID_VERSION);
        }
        String id = text.substring(1, length - 1);
        char checksum = text.charAt(length - 1);
        if (checksum != checksumProcessor.checksum(id)) {
            return new ParsingResult(ValidationError.INVALID_CHECKSUM);
        }
        return new ParsingResult(text);
    }

    public enum Target {
        BATTERY, USER
    }

    public enum ValidationError {
        INVALID, INVALID_VERSION, INVALID_CHECKSUM
    }

    public static class ParsingResult implements Serializable {
        public ValidationError error;
        public String qr;

        public ParsingResult(ValidationError error) {
            this.error = error;
        }

        public ParsingResult(String qr) {
            this.qr = qr;
        }
    }
}
