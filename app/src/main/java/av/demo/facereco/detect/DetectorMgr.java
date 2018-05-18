package av.demo.facereco.detect;

import com.tzutalin.dlib.FaceDet;

import java.io.File;

import av.demo.facereco.MyApplication;
import av.demo.facereco.R;
import av.demo.facereco.files.FileUtils;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 15/05/2018.
 */
public class DetectorMgr {
    private static final DetectorMgr sInstance = new DetectorMgr();
    private FaceDet mFaceDet;

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

}
