package av.demo.facereco;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

import av.demo.facereco.files.PictureDirCleanerTask;
import av.demo.facereco.files.PictureSaverTask;
import av.demo.facereco.images.ImageBox;
import av.demo.facereco.scheduler.Timer;
import timber.log.Timber;

/**
 * Created by Vitiello Antonio on 07/04/2018.
 */

public class TakePictureFragment extends Fragment implements Timer.Subscriber {
    private static final int TAKE_PICTURE_ALARM_ID = 1;
    private static final int FILE_CLEANER_ALARM_ID = 2;
    private CameraView mCameraView;
    private Timer mTakePictureTimer;
    private Timer mFileCleanerTimer;
    private PictureSaverTask mPictureSaverTask;
    private PictureDirCleanerTask mPictureDirCleanerTask;

    public TakePictureFragment() {
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 11/04/2018  setRetainInstance ?
//        setRetainInstance(true);

        // Start timer to take picture
        mTakePictureTimer = new Timer.Builder()
                .subscriber(this)
                .delay(getResources().getInteger(R.integer.take_picture_delay_millisec))
                .alarmId(TAKE_PICTURE_ALARM_ID)
                .build();

        // Start timer to clean piture dir
        mFileCleanerTimer = new Timer.Builder()
                .subscriber(this)
                .delay(getResources().getInteger(R.integer.file_cleaner_delay_millisec))
                .alarmId(FILE_CLEANER_ALARM_ID)
                .build();
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
    }

    public void startCamera() {
        Timber.d("Camera: starting.");
        try {
            mCameraView.start();
        } catch (final Exception exc) {
            Timber.e(exc, "Error while starting capture image.");
        }
    }

    public void stopCamera() {
        Timber.d("Camera: stopping.");
        try {
            mCameraView.stop();
        } catch (final Exception exc) {
            Timber.e(exc, "Error while stopping camera.");
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
        mTakePictureTimer.start();
        mFileCleanerTimer.start();
    }

    @Override
    public void onPause() {
        mTakePictureTimer.stop();
        mFileCleanerTimer.stop();
        stopCamera();
        super.onPause();
    }

    @Override
    public void onSchedule(int alarmId) {
        switch (alarmId) {
            case TAKE_PICTURE_ALARM_ID:
                capturePicture();
                break;
            case FILE_CLEANER_ALARM_ID:
                cleanPictureDir();
                break;
            default:
                Timber.e("Unknown alarm ID: %d", alarmId);
        }
    }

    public void capturePicture() {
        mCameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage image) {
                byte[] jpeg = image.getJpeg();
                savePicture(jpeg);
            }
        });
    }

    private void savePicture(byte[] jpeg){
        Timber.d("Save picture request");
        if(mPictureSaverTask != null) {
            mPictureSaverTask.stop();
        }
        mPictureSaverTask = new PictureSaverTask();
        mPictureSaverTask.execute(new ImageBox(jpeg));
    }

    private void cleanPictureDir() {
        Timber.d("Clean picture dir request");
        if(mPictureDirCleanerTask != null) {
            mPictureDirCleanerTask.stop();
        }
        mPictureDirCleanerTask = new PictureDirCleanerTask();
        mPictureDirCleanerTask.execute();
    }

}
