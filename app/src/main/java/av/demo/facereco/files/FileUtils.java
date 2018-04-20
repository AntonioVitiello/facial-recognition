package av.demo.facereco.files;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 11/04/2018.
 */

public class FileUtils {
    public static final int SORT_ORDER_ASCENDING = 0;
    public static final int SORT_ORDER_DESCENDING = 1;

    public static File[] listPictureSortByModified(int sortOrder) {
        return sortByModified(listPictureFiles(), sortOrder);
    }

    public static File[] listPictureFiles() {
        File pictureDir = MyApplication.getPictureDir();
        final String imageExtension = MyApplication.getStringResource(R.string.image_extension);
        File[] files;
        try {
            files = pictureDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(imageExtension);
                }

            });
        } catch (Exception exc) {
            files = new File[0];
            Timber.e(exc, "Error while reading picture dir: %s by %s", pictureDir, imageExtension);
        }
        return files;
    }

    public static File[] sortByModified(File[] files, final int sortOrder) {
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File file1, File file2) {
                return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
            }
        });
        if(sortOrder == SORT_ORDER_DESCENDING) {
            Arrays.sort(files, Collections.reverseOrder());
        }
        return files;
    }

    /**
     * Copy a raw resouce to a target path
     *
     * @param context
     * @param id         Raw resouce id eg: R.raw.shape_predictor_68_face_landmarks
     * @param targetPath File path to write
     */
    @NonNull
    public static final void copyFileFromRawToOthers(@NonNull final Context context, @RawRes int id,
                                                     @NonNull final String targetPath) {
        InputStream in = context.getResources().openRawResource(id);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(targetPath);
            byte[] buff = new byte[1024];
            int read = 0;
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } catch (Exception exc) {
            Timber.e(exc, "Error while writing file: %s", targetPath);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException exc) {
                Timber.e(exc, "Error while closing file input stream.");
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException exc) {
                Timber.e(exc, "Error while closing file output stream.");
            }
        }
    }

}
