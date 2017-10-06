package info.mschmitt.zxingsupport;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Matthias Schmitt
 */
class ZXingGlideLoader implements ModelLoader<ZXingGlideModel, InputStream> {
    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(ZXingGlideModel model, int width, int height, Options options) {
        return new LoadData<>(model, new ZXingStorageFetcher(model, width, height));
    }

    @Override
    public boolean handles(ZXingGlideModel model) {
        return true;
    }

    private class ZXingStorageFetcher implements DataFetcher<InputStream> {
        private final ZXingGlideModel model;
        private final int width;
        private final int height;
        private InputStream inputStream;

        private ZXingStorageFetcher(ZXingGlideModel model, int width, int height) {
            this.model = model;
            this.width = width;
            this.height = height;
        }

        @Override
        public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
            try {
                Bitmap bitmap = loadBitmap();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                callback.onDataReady(inputStream);
            } catch (WriterException e) {
                callback.onLoadFailed(e);
            }
        }

        private Bitmap loadBitmap() throws WriterException {
            BitMatrix bitMatrix =
                    new MultiFormatWriter().encode(model.string, model.barcodeFormat, width, height, model.hints);
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
        public void cancel() {
        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }
}
