package av.demo.facereco.files;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 11/04/2018.
 */

public class PictureDirCleanerTask extends AsyncTask<Void, Void, File[]> {
    private int mFilesRetainCount;

    @Override
    protected void onPreExecute() {
        mFilesRetainCount = MyApplication.getIntResource(R.integer.file_count_retain);
    }

    @Override
    protected File[] doInBackground(Void... voids) {
        File[] filesList = FileUtils.listPictureFiles();
        List<File> filesDeleted = new ArrayList<>();

        if(filesList.length > mFilesRetainCount){
            filesList = FileUtils.sortByModified(filesList, FileUtils.SORT_ORDER_ASCENDING);
            int filesToDeleteCount = filesList.length - mFilesRetainCount;
            for (int i = 0; i < filesToDeleteCount; i++) {
                boolean deleted = filesList[i].delete();
                if(deleted){
                    filesDeleted.add(filesList[i]);
                }
            }
        }
        return filesDeleted.toArray(new File[filesDeleted.size()]);
    }

    @Override
    protected void onPostExecute(File[] files) {
        Timber.d("Picture dir cleaner: deleted %d files.", files.length);
    }

    public void stop() {
        if(getStatus() != AsyncTask.Status.FINISHED) {
            Timber.w("Picture dir cleaner: stopping task!");
            cancel(true);
        }
    }

}
