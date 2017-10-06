package info.mschmitt.zxingsupport;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import java.io.InputStream;

/**
 * .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
 *
 * @author Matthias Schmitt
 */
@GlideModule
public class ZXingGlideModule extends LibraryGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.append(ZXingGlideModel.class, InputStream.class, ZXingGlideModel.FACTORY);
    }
}
