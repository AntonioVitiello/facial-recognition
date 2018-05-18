package av.demo.facereco.detect;

import android.content.Context;
import android.widget.ImageView;

import com.tzutalin.dlib.FaceDet;

import java.io.File;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import av.demo.facereco.files.FileUtils;
import av.demo.facereco.worker.DetectWorkerThread;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 15/05/2018.
 */
public class DetectorMgr {
    private static final DetectorMgr sInstance = new DetectorMgr();
    private FaceDet mFaceDet;
    private DetectWorkerThread mDetectThread = new DetectWorkerThread();

    public static DetectorMgr getInstance() {
        return sInstance;
    }

    private DetectorMgr() {
        // Copy file shape model just one time
        File modelFile = FileUtils.getFaceShapeModelFile();
        if (modelFile.exists()) {
            Timber.d("FaceDet: landmark model already in %s", modelFile);
        } else {
            Timber.d("FaceDet: copy landmark model to %s", modelFile);
            FileUtils.copyFromRaw(MyApplication.getContext(), R.raw.shape_predictor_68_face_landmarks, modelFile);
        }
        // Instantiate FaceDet
        mFaceDet = new FaceDet(modelFile.getPath());
    }

    public FaceDet getFaceDet() {
        return mFaceDet;
    }

    public void detectEnque(File file, ImageView imageView, Context context) {
        if(mDetectThread == null){
            mDetectThread = new DetectWorkerThread();
        }
        mDetectThread.enqueue(file, imageView, context);
    }

    public void quitDetectQueue(){
        if(mDetectThread != null){
            mDetectThread.quit();
        }
    }

}
