package av.demo.facereco.facedetect;

import android.content.Context;

import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by Antonio Vitiello on 11/04/2018.
 */

public class FaceDetectorManager {

    private static volatile FaceDetector faceDetector;
    private static Context mContext;

    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Initialize FaceDetectorManager by calling FaceDetectorManager.initialize(context).");
        }
        return mContext;
    }

    public static void initialize(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        mContext = context.getApplicationContext(); // To make it independent of activity lifecycle
    }

    private static void initDetector() {
        if (faceDetector == null) {
            synchronized (FaceDetectorManager.class) {
                if (faceDetector == null) {
                    faceDetector = new
                            FaceDetector.Builder(getContext())
                            .setTrackingEnabled(false)
                            .build();
                }
            }
        }
    }

    public static FaceDetector getFaceDetector() {
        initDetector();
        return faceDetector;
    }

    /**
     * Release the detector when you no longer need it.
     * Remember to call FaceDetectorManager.initialize(context) if you have to re-use.
     */
    public static void releaseDetector() {
        if (faceDetector != null) {
            faceDetector.release();
            faceDetector = null;
        }
        mContext = null;
    }

}