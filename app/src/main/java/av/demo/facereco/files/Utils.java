package av.demo.facereco.files;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 11/04/2018.
 */

public class Utils {
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
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File file1, File file2) {
                switch (sortOrder) {
                    case SORT_ORDER_ASCENDING:
                        return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
                    case SORT_ORDER_DESCENDING:
                    default:
                        return Long.valueOf(file2.lastModified()).compareTo(file1.lastModified());

                }
            }
        });
        return files;
    }
}
