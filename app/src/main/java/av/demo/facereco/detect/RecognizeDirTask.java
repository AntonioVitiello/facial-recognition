package av.demo.facereco.detect;

import android.os.AsyncTask;

import java.io.File;

import av.demo.facereco.files.FileUtils;

/**
 * Created by Antonio Vitiello on 24/04/2018.
 */

public class RecognizeDirTask extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        File[] files = FileUtils.listPictureFiles();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
