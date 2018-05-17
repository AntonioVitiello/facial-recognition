package av.demo.facereco.files;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import av.demo.facereco.MyApplication;
import av.demo.facereco.images.ImageBox;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 17/05/2018.
 */
public class PictureSaver {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private File mOutputFile;

    public File with(byte[] bytes) {
        File file = saveFullImage(bytes);
        return file;
    }

    public void with(ImageBox imageBox) {
        saveTransformedImage(imageBox.getBitmap(), imageBox.getFile());
    }

    private File saveFullImage(byte[] bytes) {
        FileOutputStream output = null;
        File file = null;
        try {
            file = getOutputFile();
            output = new FileOutputStream(file);
            output.write(bytes);
            output.flush();
        } catch (Exception exc) {
            Timber.e(exc, "Picture saver: Error while saving full size image: " + file);
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Picture saver: full size picture saved in: %s", file);
                } catch (IOException exc) {
                    Timber.e(exc, "Picture saver: Error while closing full size image stream: " + file);
                }
            }
        }
        return file;
    }

    private File getOutputFile() {
        File parentDir = MyApplication.getPictureDir();
        String filename = mDateFormat.format(new Date()) + ".jpg";
        return new File(parentDir, filename);
    }

    private void saveTransformedImage(Bitmap bitmap, File file) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (Exception exc) {
            Timber.e(exc, "Picture saver: Error while saving transformed bitmap: " + file);
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Picture saver: transformed bitmap[%dH,%dW] saved in: %s",
                            bitmap.getHeight(), bitmap.getWidth(), file);
                } catch (IOException exc) {
                    Timber.e(exc, "Picture saver: Error while closing transformed bitmap stream: " + file);
                }
            }
        }
    }

}
