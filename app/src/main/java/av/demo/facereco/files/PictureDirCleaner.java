package av.demo.facereco.files;

import java.io.File;

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
            File[] files = Utils.listPictureFiles();

            if(files.length > filesRetainCount){
                files = Utils.sortByModified(files, Utils.SORT_ORDER_ASCENDING);
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
