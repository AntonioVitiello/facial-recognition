package av.demo.facereco.files;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import av.demo.facereco.MyApplication;
import av.demo.facereco.images.ImageBox;
import av.demo.facereco.images.ImageUtils;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 17/04/2018.
 */

public class PictureSaverTask extends AsyncTask<ImageBox, Void, File> {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private File mOutputFile;


    @Override
    protected File doInBackground(ImageBox... imageBoxes) {
        return saveImage(imageBoxes[0].getBytes());
    }

    @Override
    protected void onPostExecute(final File outputFile) {
        // Resize picture and save on FS
        ImageUtils imageUtils = ImageUtils.getInstance();
        imageUtils.resizePicture(outputFile, new ImageUtils.OnImageReady() {
            @Override
            public void setBitmap(Bitmap bitmap) {
                saveBitmap(outputFile, bitmap);
            }
        });
    }

    private File saveImage(byte[] bytes) {
        FileOutputStream output = null;
        try {
            mOutputFile = getOutputFile();
            output = new FileOutputStream(mOutputFile);
            output.write(bytes);
            output.flush();
        } catch (Exception exc) {
            Timber.e(exc, "Error while saving image: " + mOutputFile);
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Picture pre-saved in: %s", mOutputFile);
                } catch (IOException exc) {
                    Timber.e(exc, "Error while closing image output stream: " + mOutputFile);
                }
            }
        }
        return mOutputFile;
    }

    private File getOutputFile() {
        File parentDir = MyApplication.getPictureDir();
        String filename = mDateFormat.format(new Date()) + ".jpg";
        return new File(parentDir, filename);
    }

    private static void saveBitmap(File outputFile, Bitmap bitmap) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        } catch (Exception exc) {
            Timber.e(exc, "Error while saving bitmap: " + outputFile);
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Bitmap resized[%2$dH,%3$dW] and saved in: %s", outputFile, bitmap.getHeight(), bitmap.getWidth());
                } catch (IOException exc) {
                    Timber.e(exc, "Error while closing bitmap output stream: " + outputFile);
                }
            }
        }
    }

    public void cleanOutputFile() {
        if (mOutputFile != null && mOutputFile.exists()) {
            try {
                if (mOutputFile.delete()) {
                    mOutputFile = null;
                }
            } catch (SecurityException exc) {
                Timber.e(exc, "Error while deleting file: %s", mOutputFile);
                if (mOutputFile != null && mOutputFile.exists()) {
                    try {
                        mOutputFile.deleteOnExit();
                    } catch (SecurityException e) {
                        Timber.e(e, "Error while setting deleteOnExit on file: %s", mOutputFile);
                    }
                }
            }
        }
    }

}
