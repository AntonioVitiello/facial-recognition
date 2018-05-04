package av.demo.facereco.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public static ImageUtils sInstance = new ImageUtils();
    private final Picasso mPicasso;
    private final int mTargetMaxSize;

    public interface OnImageReady {
        void setBitmap(Bitmap bitmap);
    }


    private ImageUtils() {
        mPicasso = Picasso.get();
        mTargetMaxSize = MyApplication.getIntResource(R.integer.image_target_max_size);
    }

    public static ImageUtils getInstance() {
        return sInstance;
    }

    public static int getTargetMaxSize() {
        return sInstance.mTargetMaxSize;
    }

    public void resizePicture(final File file, final OnImageReady onImageReady) {
        mPicasso.load(file)
                .resize(getTargetMaxSize(), getTargetMaxSize())
                .centerInside()
                .transform(new GrayscaleTransformation())
                .into(new Target() {
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
                });
    }

    public Bitmap resizeImage(byte[] imageAsBytes, int dstHeight, int dstWidth) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
    }

}
