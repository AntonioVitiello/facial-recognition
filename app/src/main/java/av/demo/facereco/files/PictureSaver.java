package av.demo.facereco.files;

import android.graphics.Bitmap;

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
    private Bitmap mBitmap;
    private boolean isBitmap;

    public PictureSaver() {
        // Do nothing
    }

    public void setImage(byte[] image) {
        mImage = image;
        isBitmap = false;
    }

    // TODO: 12/04/2018 NOT USED
    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
        isBitmap = true;
    }

    private File getOutputFile() {
        File parentDir = MyApplication.getPictureDir();
        String filename = mDateFormat.format(new Date()) + ".jpg";
        return new File(parentDir, filename);
    }

    @Override
    public void run() {
        if(isBitmap){
            saveBitmap();
        } else {
            saveImage();
        }
    }

    private void saveImage() {
        FileOutputStream output = null;
        File file = null;
        try {
            file = getOutputFile();
            output = new FileOutputStream(file);
            output.write(mImage);
            output.flush();
        } catch (Exception exc) {
            Timber.e(exc, "Error while saving image: " + file.getPath());
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Image file saved: " + file.getPath());
                } catch (IOException exc) {
                    Timber.e(exc, "Error while closing image output stream: " + file.getPath());
                }
            }
        }
    }

    // TODO: 12/04/2018 NOT USED
    private void saveBitmap() {
        FileOutputStream output = null;
        File file = null;
        try {
            file = getOutputFile();
            output = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (Exception exc) {
            Timber.e(exc, "Error while saving bitmap: " + file.getPath());
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Bitmap saved: " + file.getPath());
                } catch (IOException exc) {
                    Timber.e(exc, "Error while closing bitmap output stream: " + file.getPath());
                }
            }
        }
    }

}
