package com.tzutalin.dlib;

import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

/**
 * Created by houzhi on 16-10-20.
 * Modified by tzutalin on 16-11-15
 */
public class FaceDet {

    // accessed by native methods
    @SuppressWarnings("unused")
    private long mNativeFaceDetContext;
    private String mLandMarkPath = "";

    static {
        try {
            System.loadLibrary("android_dlib");
            jniNativeClassInit();
            Timber.d("jniNativeClassInit success");
        } catch (UnsatisfiedLinkError exc) {
            Timber.e(exc, "library not found");
        }
    }

    @SuppressWarnings("unused")
    public FaceDet() {
        jniInit(mLandMarkPath);
    }

    public FaceDet(String landMarkPath) {
        mLandMarkPath = landMarkPath;
        jniInit(mLandMarkPath);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detect(@NonNull String path) {
        VisionDetRet[] detRets = jniDetect(path);
        return Arrays.asList(detRets);
    }

    @Nullable
    @WorkerThread
    public List<VisionDetRet> detect(@NonNull Bitmap bitmap) {
        VisionDetRet[] detRets = jniBitmapDetect(bitmap);
        return Arrays.asList(detRets);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        release();
    }

    public void release() {
        jniDeInit();
    }

    @Keep
    private native static void jniNativeClassInit();

    @Keep
    private synchronized native int jniInit(String landmarkModelPath);

    @Keep
    private synchronized native int jniDeInit();

    @Keep
    private synchronized native VisionDetRet[] jniBitmapDetect(Bitmap bitmap);

    @Keep
    private synchronized native VisionDetRet[] jniDetect(String path);
}