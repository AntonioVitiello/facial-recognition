package av.demo.facereco;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;
import java.util.Timer;

import av.demo.facereco.files.PictureDirCleanerTask;
import av.demo.facereco.images.ImageBox;
import av.demo.facereco.images.ImageUtils;
import av.demo.facereco.timertask.MyTimerTask;
import av.demo.facereco.worker.MyWorkerThread;
import timber.log.Timber;

/**
 * Created by Vitiello Antonio on 07/04/2018.
 */

public class TakePictureFragment extends Fragment implements MyTimerTask.OnTimer, MyWorkerThread.OnResponse {
    public static final long PIC_INTERVAL = MyApplication.getIntResource(R.integer.take_picture_interval_millisec);
    public static final long CLEAN_INTERVAL = MyApplication.getIntResource(R.integer.files_cleaner_interval_millisec);
    public static final int PIC_TASK_ID = 0;
    public static final int CLEAN_TASK_ID = 1;

    private CameraView mCameraView;
    private PictureDirCleanerTask mPictureDirCleanerTask;
    private Timer mTimer;
    private MyTimerTask mTakePictureTask;
    private MyTimerTask mFilesCleanerTask;
    private MyWorkerThread mWorkerThread;

    public TakePictureFragment() {
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 11/04/2018  setRetainInstance ?
//        setRetainInstance(true);

        mTimer = new Timer(getClass().getSimpleName(), true);
        mWorkerThread = new MyWorkerThread(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_take_picture, container, false);
        initCamera(rootView);
        return rootView;
    }

    private void initCamera(View rootView) {
        mCameraView = rootView.findViewById(R.id.camera);
        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onCameraOpened(CameraOptions options) {
                super.onCameraOpened(options);
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
            }

            @Override
            public void onPictureTaken(byte[] jpeg) {
                savePicture(jpeg);
            }

            @Override
            public void onOrientationChanged(int orientation) {
                super.onOrientationChanged(orientation);
            }
        });

    }

    private void startTimers() {
        stopTimers();
        mTakePictureTask = new MyTimerTask(this, PIC_TASK_ID);
        mFilesCleanerTask = new MyTimerTask(this, CLEAN_TASK_ID);
        mTimer.schedule(mTakePictureTask, PIC_INTERVAL, PIC_INTERVAL);
        mTimer.schedule(mFilesCleanerTask, CLEAN_INTERVAL, CLEAN_INTERVAL);
    }

    private void stopTimers() {
        if (mTakePictureTask != null) {
            mTakePictureTask.cancel();
        }
        if (mFilesCleanerTask != null) {
            mFilesCleanerTask.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
        startTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
        stopTimers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mCameraView.destroy();
        mWorkerThread.quitSafely();
    }

    /**
     * CamerView implements checkPermissions in his with() method so jus need manage onRequestPermissionsResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !mCameraView.isStarted()) {
            mCameraView.start();
        }
    }

    private void savePicture(byte[] jpeg) {
        mWorkerThread.enqueue(jpeg, MyWorkerThread.SAVE_PICTURE_JOB);
    }

    @Override
    public void onTimeout(int taskId) {
        switch (taskId) {
            case PIC_TASK_ID:
                capturePicture();
                break;
            case CLEAN_TASK_ID:
                cleanPictureDir();
                break;
            default:
                Timber.e("Unknown alarm ID: %d", taskId);
        }
    }

    public void capturePicture() {
        Timber.d("Capture picture request");
        mCameraView.capturePicture();
    }

    private void cleanPictureDir() {
        Timber.d("Clean picture dir request");


        mPictureDirCleanerTask = new PictureDirCleanerTask();
        mPictureDirCleanerTask.execute();
    }

    @Override
    public void onSaved(Object obj, int jobId) {
        switch (jobId) {
            case MyWorkerThread.SAVE_PICTURE_JOB: {
                final File file = (File) obj;
                Timber.d("Full size picture saved: %s", file);
                // Reload picture, caches it, resize and save in gray scale. This must happens in main-Thread!
                ImageUtils.transformPicture(file, new ImageUtils.OnImageReady() {
                    @Override
                    public void setBitmap(Bitmap bitmap) {
                        ImageBox imageBox = new ImageBox(bitmap, file);
                        mWorkerThread.enqueue(imageBox, MyWorkerThread.SAVE_TRANSF_PICTURE_JOB);
                    }
                });
                break;
            }
            case MyWorkerThread.CLEAN_PIC_DIR_JOB: {

                break;
            }
            case MyWorkerThread.SAVE_TRANSF_PICTURE_JOB: {
                final File file = (File) obj;
                Timber.d("Picture transformed: %s", file);
                break;
            }
            default:
                Timber.e("Unknown job id: %d", jobId);
        }

    }
}
