package av.demo.facereco.scheduler;

import android.os.Handler;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 06/04/2018.
 */

public class FileCleanerScheduler {
    private static final FileCleanerScheduler sInstance = new FileCleanerScheduler();
    final int mDelay = MyApplication.getIntResource(R.integer.file_cleaner_delay_millisec);
    final int mFilesRetainCount = MyApplication.getIntResource(R.integer.file_count_retain);
    private final Object lock = new Object();
    private File mOutputDir;
    private boolean isStarted;
    private boolean startRequest;

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            try {
                if (startRequest) {
                    new Thread(fileCleanerRunnable).start();
                    Timber.d("File cleaner daemon: cleaning older files.");
                    isStarted = timerHandler.postDelayed(this, mDelay);
                } else {
                    isStarted = false;
                    Timber.d("File cleaner daemon: stopped.");
                }
            } catch (NullPointerException exc) {
                isStarted = false;
                Timber.d("File cleaner daemon: interrupted.");
            } catch (Exception exc) {
                isStarted = false;
                Timber.e(exc, "File cleaner daemon: error.");
            }
        }
    };

    Runnable fileCleanerRunnable = new Runnable() {
        @Override
        public void run() {
            File[] files = mOutputDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });

            if(files.length > mFilesRetainCount){
                Arrays.sort(files, new Comparator<File>(){
                    public int compare(File file1, File file2) {
                        return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());
                    }
                });
                int filesToDeleteCount = files.length - mFilesRetainCount;
                int filesDeletedCount = 0;
                for (int i = 0; i < filesToDeleteCount; i++) {
                    boolean deleted = files[i].delete();
                    if(deleted){
                        ++filesDeletedCount;
                    }
                }
                Timber.d("File cleaner daemon: cleaned %d files.", filesDeletedCount);
            }
        }
    };

    private FileCleanerScheduler() {
        // Do nothing
    }

    public static FileCleanerScheduler getInstance() {
        return sInstance;
    }

    public void start(File outputDir) {
        startRequest = true;
        if (isStarted) {
            Timber.d("File cleaner daemon: already started.");
        } else {
            synchronized(lock) {
                if (!isStarted) {
                    mOutputDir = outputDir;
                    isStarted = timerHandler.postDelayed(timerRunnable, mDelay);
                    Timber.d("File cleaner daemon: start request.");
                }
            }
        }
    }

    public void start() {
        start(mOutputDir);
    }

    public void stop() {
        if (startRequest) {
            startRequest = false;
            Timber.d("File cleaner daemon: stop request.");
        } else {
            Timber.d("File cleaner daemon: ignored new stop request.");
        }
    }

    private long startTime(){
        return System.currentTimeMillis();
    }

    private String stopTime(long startTime){
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d,%03d", minutes, seconds, millis);
    }

}
