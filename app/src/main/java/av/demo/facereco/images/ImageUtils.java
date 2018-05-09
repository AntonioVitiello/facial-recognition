package av.demo.facereco.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.media.ExifInterface;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

import static android.support.media.ExifInterface.ORIENTATION_NORMAL;
import static android.support.media.ExifInterface.TAG_ORIENTATION;

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

    /**
     * Reload picture, caches it, resize and save in gray scale. This must happens in main-Thread!
     * @param file picture
     * @param onImageReady Callback
     */
    public void transformPicture(final File file, final OnImageReady onImageReady) {
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

    public void transformPictureRotate(final File file, final OnImageReady onImageReady) {
        float rotation = 0;
        try {
            Uri uri = Uri.fromFile(file);
            ExifInterface exifInterface = new ExifInterface(uri.getPath());
            int orientation = exifInterface.getAttributeInt(TAG_ORIENTATION, ORIENTATION_NORMAL);
            //Exif Orientation Tag: http://sylvana.net/jpegcrop/exif_orientation.html
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90: {
                    rotation = -90f;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_180: {
                    rotation = -180f;
                    break;
                }
                case ExifInterface.ORIENTATION_ROTATE_270: {
                    rotation = 270f;
                    break;
                }
            }
        } catch (IOException exc) {
            Timber.e(exc, "Error while reading EXIF info from: %s", file);
        }

        mPicasso.load(file)
                .resize(getTargetMaxSize(), getTargetMaxSize())
                .centerInside()
                .rotate(rotation)
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
