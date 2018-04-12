package av.demo.facereco;

import android.graphics.Bitmap;
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

import av.demo.facereco.files.PictureDirCleaner;
import av.demo.facereco.files.PictureSaver;
import av.demo.facereco.scheduler.Timer;
import timber.log.Timber;

/**
 * Created by Vitiello Antonio on 07/04/2018.
 */

public class TakePictureFragment extends Fragment implements Timer.Subscriber {
    private static final int TAKE_PICTURE_ALARM_ID = 1;
    private static final int FILE_CLEANER_ALARM_ID = 2;
    private CameraView mCameraView;
    private PictureSaver mPictureSaver;
    private Timer mTakePictureTimer;
    private Timer mFileCleanerTimer;
    private PictureDirCleaner mPictureDirCleaner;

    public TakePictureFragment() {
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 11/04/2018  setRetainInstance ?
//        setRetainInstance(true);

        // Instantiate picture file saver
        mPictureSaver = new PictureSaver();

        // Instantiate picture dir cleaner
        mPictureDirCleaner = new PictureDirCleaner();

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
                captureImage();
                break;
            case FILE_CLEANER_ALARM_ID:
                cleanPictureDir();
                break;
            default:
                Timber.e("Unknown alarm ID: %d", alarmId);
        }
    }

    // TODO: 12/04/2018 NOT USED
    public void captureBitmap() {
        mCameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage image) {
                try {
                    Bitmap bitmap = image.getBitmap();
                    mPictureSaver.setImage(bitmap);
                    new Thread(mPictureSaver).run();
                } catch (Exception exc) {
                    Timber.e(exc, "Error while capturing image.");
                }
            }
        });
    }

    public void captureImage() {
        mCameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
            @Override
            public void callback(CameraKitImage image) {
                try {
                    byte[] jpeg = image.getJpeg();
                    mPictureSaver.setImage(jpeg);
                    new Thread(mPictureSaver).run();
                } catch (Exception exc) {
                    Timber.e(exc, "Error while capturing image.");
                }
            }
        });
    }

    private void cleanPictureDir() {
        Timber.d("Clean picture dir request");
        if(mPictureDirCleaner.isAlive()){
            mPictureDirCleaner.stop();
        }
        mPictureDirCleaner.start();
    }

}
