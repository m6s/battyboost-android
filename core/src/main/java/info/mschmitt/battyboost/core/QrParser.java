package info.mschmitt.battyboost.core;

import info.mschmitt.battyboost.core.utils.ChecksumProcessor;

import java.io.Serializable;

/**
 * @author Matthias Schmitt
 */
public class QrParser {
    private static final String PREFIX = "https://battyboost.com/qr?";
    private final ChecksumProcessor checksumProcessor = new ChecksumProcessor();

    public ParsingResult parse(String text) {
        int length = text.length();
        if (length != PREFIX.length() + 12 && length != PREFIX.length() + 11) {
            return new ParsingResult(ValidationError.INVALID);
        }
        if (!text.startsWith(PREFIX)) {
            return new ParsingResult(ValidationError.INVALID);
        }
        String qrId = text.substring(PREFIX.length());
        char version = qrId.charAt(0);
        if (version != 0) {
            return new ParsingResult(ValidationError.INVALID_VERSION);
        }
        String id = qrId.substring(1, length - 1);
        char checksum = qrId.charAt(length - 1);
        if (checksum != checksumProcessor.checksum(id)) {
            return new ParsingResult(ValidationError.INVALID_CHECKSUM);
        }
        return new ParsingResult(qrId);
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
