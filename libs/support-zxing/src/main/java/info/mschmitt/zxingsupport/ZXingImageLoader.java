package info.mschmitt.zxingsupport;

import android.graphics.Bitmap;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthias Schmitt
 */
public class ZXingImageLoader implements StreamModelLoader<String> {
    private final Map<EncodeHintType, Object> hints = new HashMap<>();
    private final BarcodeFormat barcodeFormat;

    private ZXingImageLoader(BarcodeFormat barcodeFormat, ErrorCorrectionLevel errorCorrectionLevel, int margin) {
        this.barcodeFormat = barcodeFormat;
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, margin);
    }

    public static ZXingImageLoader create(BarcodeFormat barcodeFormat, ErrorCorrectionLevel errorCorrectionLevel,
                                          int margin) {
        return new ZXingImageLoader(barcodeFormat, errorCorrectionLevel, margin);
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(String content, int width, int height) {
        return new ZXingStorageFetcher(content, width, height);
    }

    private class ZXingStorageFetcher implements DataFetcher<InputStream> {
        private final int width;
        private final int height;
        private String content;
        private InputStream inputStream;

        private ZXingStorageFetcher(String content, int width, int height) {
            this.content = content;
            this.width = width;
            this.height = height;
        }

        @Override
        public InputStream loadData(Priority priority) throws Exception {
            if (content == null) {
                throw new NullPointerException();
            }
            Bitmap bitmap = loadBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            return inputStream;
        }

        private Bitmap loadBitmap() throws WriterException {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, barcodeFormat, width, height, hints);
            int bitMatrixWidth = bitMatrix.getWidth();
            int bitMatrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
            for (int y = 0; y < bitMatrixHeight; y++) {
                int offset = y * bitMatrixWidth;
                for (int x = 0; x < bitMatrixWidth; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xff000000 : 0xffffffff;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
            bitmap.setPixels(pixels, 0, bitMatrixWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
            return bitmap;
        }

        @Override
        public void cleanup() {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException ignored) {
                }
            }
        }

        @Override
        public String getId() {
            return content;
        }

        @Override
        public void cancel() {
        }
    }
}
