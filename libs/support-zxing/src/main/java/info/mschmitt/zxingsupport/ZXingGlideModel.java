package info.mschmitt.zxingsupport;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matthias Schmitt
 */
public class ZXingGlideModel implements Key {
    static final ModelLoaderFactory<ZXingGlideModel, InputStream> FACTORY =
            new ModelLoaderFactory<ZXingGlideModel, InputStream>() {
                @Override
                public ModelLoader<ZXingGlideModel, InputStream> build(MultiModelLoaderFactory multiFactory) {
                    return new ZXingGlideLoader();
                }

                @Override
                public void teardown() {
                }
            };
    final String string;
    final Map<EncodeHintType, Object> hints = new HashMap<>();
    final BarcodeFormat barcodeFormat;

    private ZXingGlideModel(String string, BarcodeFormat barcodeFormat, ErrorCorrectionLevel errorCorrectionLevel,
                            int margin) {
        this.string = string;
        this.barcodeFormat = barcodeFormat;
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, margin);
    }

    public static ZXingGlideModel qr(String string) {
        return new ZXingGlideModel(string, BarcodeFormat.QR_CODE, ErrorCorrectionLevel.L, 0);
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ZXingGlideModel && string.equals(((ZXingGlideModel) obj).string);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(string.getBytes());
    }
}
