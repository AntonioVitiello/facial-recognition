package av.demo.facereco.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 17/05/2018.
 */
public class PictureCleaner {
    public static final int FILES_RETAIN_COUNT = MyApplication.getIntResource(R.integer.file_count_retain);

    public static File[] clean() {
        File[] filesList = FileUtils.listPictureFiles();
        List<File> filesDeleted = new ArrayList<>();

        if(filesList.length > FILES_RETAIN_COUNT){
            filesList = FileUtils.sortByModified(filesList, FileUtils.SORT_ORDER_ASCENDING);
            int filesToDeleteCount = filesList.length - FILES_RETAIN_COUNT;
            for (int i = 0; i < filesToDeleteCount; i++) {
                boolean deleted = filesList[i].delete();
                if(deleted){
                    filesDeleted.add(filesList[i]);
                }
            }
        }

        File[] files = filesDeleted.toArray(new File[filesDeleted.size()]);
        Timber.d("Picture dir cleaner: deleted %d files.", files.length);
        return files;
    }

}
