package av.demo.facereco;

import android.content.Context;

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
    private final Context mContext;
    private byte[] mImage;

    public ImageSaver(Context context) {
        mContext = context;
    }

    public void setImage(byte[] image) {
        mImage = image;
    }

    private File getOutputFile() {
        String filename = mDateFormat.format(new Date()) + ".jpg";
        return new File(mContext.getExternalCacheDir(), filename);
    }

    @Override
    public void run() {
        FileOutputStream output = null;
        File mFile = null;
        try {
            mFile = getOutputFile();
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
