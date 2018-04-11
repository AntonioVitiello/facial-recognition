package av.demo.facereco.files;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 11/04/2018.
 */

public class PictureDirCleaner {
    Runnable mFileCleanerRunnable = new Runnable() {
        @Override
        public void run() {
            int filesRetainCount = MyApplication.getIntResource(R.integer.file_count_retain);
            File parentDir = MyApplication.getmExternalCacheDir();
            File[] files = parentDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });

            if(files.length > filesRetainCount){
                Arrays.sort(files, new Comparator<File>(){
                    public int compare(File file1, File file2) {
                        return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
                    }
                });
                int filesToDeleteCount = files.length - filesRetainCount;
                int filesDeletedCount = 0;
                for (int i = 0; i < filesToDeleteCount; i++) {
                    boolean deleted = files[i].delete();
                    if(deleted){
                        ++filesDeletedCount;
                    }
                }
                Timber.d("Picture dir cleaner: deleted %d files.", filesDeletedCount);
            }
        }
    };
    private Thread mThread;

    public void start(){
        mThread = new Thread(mFileCleanerRunnable);
        mThread.start();
    }

    public boolean isAlive(){
        return mThread == null || mThread.isAlive();
    }

    public void stop(){
        if(mThread != null) {
            mThread.interrupt();
        }
    }

}
