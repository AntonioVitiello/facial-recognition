package av.demo.facereco.images;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 17/04/2018.
 */

public class ImageUtils {
    public static final int TARGET_MAX_SIZE = MyApplication.getIntResource(R.integer.image_target_max_size);
    public static final CacheTarget sCacheTarget = new CacheTarget();

    public interface OnImageReady {
        void setBitmap(Bitmap bitmap);
    }


    /**
     * Reload picture, caches it, resize and save in gray scale. This must happens in main-Thread!
     *
     * @param file         picture
     * @param onImageReady Callback
     */
    public static void transformPicture(File file, OnImageReady onImageReady) {
        Picasso.get()
                .load(file)
                .resize(TARGET_MAX_SIZE, TARGET_MAX_SIZE)
                .centerInside()
                .transform(new GrayscaleTransformation())
                .into(sCacheTarget.set(file, onImageReady));
    }

    private static class CacheTarget implements Target {
        private OnImageReady onImageReady;
        private File file;

        public CacheTarget set(File file, OnImageReady onImageReady) {
            this.file = file;
            this.onImageReady = onImageReady;
            return CacheTarget.this;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            onImageReady.setBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception exc, Drawable errorDrawable) {
            Timber.e(exc, "Error while loading picture file: %s", file);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }

/*
    public Bitmap resizeImage(byte[] imageAsBytes, int dstHeight, int dstWidth) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
    }
*/

}
