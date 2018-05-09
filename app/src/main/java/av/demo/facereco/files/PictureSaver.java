package av.demo.facereco.files;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import av.demo.facereco.MyApplication;
import av.demo.facereco.images.ImageUtils;
import io.fotoapparat.result.PendingResult;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.result.WhenDoneListener;
import kotlin.Unit;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 17/04/2018.
 */

public class PictureSaver {
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyMMdd_HHmmss");
    private File mOutputFile;

    public void saveToFile(PhotoResult photoResult) {
        mOutputFile = getOutputFile();

        // First save picture file at full-size
        PendingResult<Unit> unitPendingResult = photoResult.saveToFile(mOutputFile);
        unitPendingResult.whenDone(new WhenDoneListener<Unit>() {
            @Override
            public void whenDone(Unit unit) {
                Timber.d("Picture file saved in %s", mOutputFile);
                ImageUtils imageUtils = ImageUtils.getInstance();
                imageUtils.transformPicture(mOutputFile, new ImageUtils.OnImageReady() {
                    @Override
                    public void setBitmap(final Bitmap bitmap) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                saveBitmap(mOutputFile, bitmap);
                            }
                        }).start();
                    }
                });
            }
        });
    }

    private File getOutputFile() {
        File parentDir = MyApplication.getPictureDir();
        String filename = mDateFormat.format(new Date()) + ".jpg";
        return new File(parentDir, filename);
    }

    private static void saveBitmap(final File outputFile, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                } catch (Exception exc) {
                    Timber.e(exc, "Picture saver: Error while saving bitmap: " + outputFile);
                } finally {
                    if (output != null) {
                        try {
                            output.close();
                            Timber.d("Picture saver: Bitmap resized[%dH,%dW] and saved in: %s",
                                    bitmap.getHeight(), bitmap.getWidth(), outputFile);
                        } catch (IOException exc) {
                            Timber.e(exc, "Picture saver: Error while closing bitmap output stream: " + outputFile);
                        }
                    }
                }
            }
        }).start();
    }

    private File saveImageRaw(byte[] bytes) {
        FileOutputStream output = null;
        File file = null;
        try {
            file = getOutputFile();
            output = new FileOutputStream(file);
            output.write(bytes);
            output.flush();
        } catch (Exception exc) {
            Timber.e(exc, "Picture saver: Error while saving image: " + file);
        } finally {
            if (output != null) {
                try {
                    output.close();
                    Timber.d("Picture saver: Picture pre-saved in: %s", file);
                } catch (IOException exc) {
                    Timber.e(exc, "Picture saver: Error while closing image output stream: " + file);
                }
            }
        }
        return file;
    }

    public void cleanOutputFile() {
        if (mOutputFile != null && mOutputFile.exists()) {
            try {
                if (mOutputFile.delete()) {
                    mOutputFile = null;
                }
            } catch (SecurityException exc) {
                Timber.e(exc, "Picture saver: Error while deleting file: %s", mOutputFile);
                if (mOutputFile != null && mOutputFile.exists()) {
                    try {
                        mOutputFile.deleteOnExit();
                    } catch (SecurityException e) {
                        Timber.e(e, "Picture saver: Error while setting deleteOnExit on file: %s", mOutputFile);
                    }
                }
            }
        }
    }

}
