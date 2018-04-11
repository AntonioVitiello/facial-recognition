package av.demo.facereco.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import av.demo.facereco.MyApplication;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 05/04/2018.
 */

public class PictureSaver implements Runnable {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private byte[] mImage;

    public PictureSaver() {
        // Do nothing
    }

    public void setImage(byte[] image) {
        mImage = image;
    }

    private File getOutputFile() {
        File parentDir = MyApplication.getmExternalCacheDir();
        String filename = mDateFormat.format(new Date()) + ".jpg";
        return new File(parentDir, filename);
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
        } catch (Exception exc) {
            Timber.e(exc, "Error while saving image: " + mFile.toString());
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Image file saved: " + mFile.toString());
                } catch (IOException exc) {
                    Timber.e(exc, "Error while closing image output stream: " + mFile.toString());
                }
            }
        }
    }

}
