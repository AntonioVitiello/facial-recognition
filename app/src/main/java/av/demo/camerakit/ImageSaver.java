package av.demo.camerakit;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 05/04/2018.
 */

public class ImageSaver implements Runnable {
    private static final String LOG_TAG = "ImageSaver";
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private final byte[] mImage;
    private final Context mContext;
    private final File mFile;

    public ImageSaver(Context context, byte[] image) {
        mContext = context;
        mImage = image;
        mFile = getOutputMediaFile((ContextWrapper) context);
    }

    private File getOutputMediaFile(ContextWrapper context) {
        String filename = mDateFormat.format(new Date()) + ".jpg";
        File outputMediaFile = new File(context.getExternalCacheDir(), filename);
        return outputMediaFile;
    }

    public File getPictureFile() {
        return mFile;
    }

    @Override
    public void run() {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(mFile);
            output.write(mImage);
            output.flush();
        } catch (Exception e) {
            Timber.e("Error while saving image: " + mFile.toString(), e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Image file saved: " + mFile.toString());
                } catch (IOException e) {
                    Timber.e("Error while closing image output stream: " + mFile.toString(), e);
                }
            }
        }
    }

}
