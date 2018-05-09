package av.demo.facereco;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import av.demo.facereco.dialogs.ErrorDialog;
import av.demo.facereco.dialogs.RationaleDialog;
import av.demo.facereco.files.PictureDirCleanerTask;
import av.demo.facereco.files.PictureSaver;
import av.demo.facereco.scheduler.Timer;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.error.CameraErrorListener;
import io.fotoapparat.exception.camera.CameraException;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.preview.FrameProcessor;
import io.fotoapparat.result.PhotoResult;
import io.fotoapparat.view.CameraView;
import timber.log.Timber;

import static io.fotoapparat.selector.AspectRatioSelectorsKt.standardRatio;
import static io.fotoapparat.selector.FlashSelectorsKt.off;
import static io.fotoapparat.selector.FocusModeSelectorsKt.autoFocus;
import static io.fotoapparat.selector.FocusModeSelectorsKt.continuousFocusPicture;
import static io.fotoapparat.selector.FocusModeSelectorsKt.fixed;
import static io.fotoapparat.selector.LensPositionSelectorsKt.front;
import static io.fotoapparat.selector.PreviewFpsRangeSelectorsKt.highestFps;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;
import static io.fotoapparat.selector.SelectorsKt.firstAvailable;
import static io.fotoapparat.selector.SensorSensitivitySelectorsKt.highestSensorSensitivity;

/**
 * Created by Vitiello Antonio on 07/04/2018.
 */

public class TakePictureFragment extends Fragment implements Timer.Subscriber {
    private static final int TAKE_PICTURE_ALARM_ID = 1;
    private static final int FILE_CLEANER_ALARM_ID = 2;
    private static final String FRAGMENT_DIALOG_TAG = "dialog";
    private CameraView mCameraView;
    private Timer mTakePictureTimer;
    private Timer mFileCleanerTimer;
    private Thread mPictureSaverTask;
    private PictureDirCleanerTask mPictureDirCleanerTask;
    private Fotoapparat mFotoapparat;
    private int CAMERA_PERMISSION_CODE = 11;

    public TakePictureFragment() {
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 11/04/2018  setRetainInstance ?
//        setRetainInstance(true);

        checkPermissions();

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

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String cameraPermission = Manifest.permission.CAMERA;
            if (shouldShowRequestPermissionRationale(cameraPermission)) {
                RationaleDialog.newInstance(cameraPermission, CAMERA_PERMISSION_CODE, getString(R.string.camera_request_permission))
                        .show(getFragmentManager(), FRAGMENT_DIALOG_TAG);
            } else {
                requestPermissions(new String[]{cameraPermission}, CAMERA_PERMISSION_CODE);
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed, only on SDK M or later...
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.camera_denied_permission))
                        .show(getFragmentManager(), FRAGMENT_DIALOG_TAG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_take_picture, container, false);
        mCameraView = rootView.findViewById(R.id.cameraView);
        initCamera();
        return rootView;
    }

    private void initCamera() {
        mFotoapparat = Fotoapparat
                .with(getContext())
                .photoResolution(standardRatio(highestResolution()
                ))
                .into(mCameraView)
                .previewScaleType(ScaleType.CenterInside)
                .lensPosition(front())
                .focusMode(firstAvailable(continuousFocusPicture(), autoFocus(), fixed()))
                .flash(off())
                .previewFpsRange(highestFps())
                .sensorSensitivity(highestSensorSensitivity())
                // .frameProcessor(new SampleFrameProcessor())
                .cameraErrorCallback(new CameraErrorListener() {
                    @Override
                    public void onError(@NotNull CameraException exc) {
                        Timber.e(exc, "Camera error.");
                    }
                })
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("Camera: starting.");
        mFotoapparat.start();
        mTakePictureTimer.start();
        mFileCleanerTimer.start();
    }

    @Override
    public void onPause() {
        Timber.d("Camera: stopping.");
        mFotoapparat.stop();
        mTakePictureTimer.stop();
        mFileCleanerTimer.stop();
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
        Timber.d("Take picture request");
        PhotoResult photoResult = mFotoapparat.takePicture();
        new PictureSaver().saveToFile(photoResult);
    }

    private void cleanPictureDir() {
        Timber.d("Clean picture dir request");
        if(mPictureDirCleanerTask != null) {
            mPictureDirCleanerTask.stop();
        }
        mPictureDirCleanerTask = new PictureDirCleanerTask();
        mPictureDirCleanerTask.execute();
    }

    private class SampleFrameProcessor implements FrameProcessor {
        @Override
        public void process(@NotNull Frame frame) {
            // Perform frame processing, if needed
        }
    }

}
