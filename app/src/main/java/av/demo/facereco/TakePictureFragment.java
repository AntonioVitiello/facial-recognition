package av.demo.facereco;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;

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
    private boolean mCapturingPicture;

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

    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
        mTakePictureTimer.start();
        mFileCleanerTimer.start();
    }

    @Override
    public void onPause() {
        mTakePictureTimer.stop();
        mFileCleanerTimer.stop();
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }

    /**
     * CamerView implements checkPermissions in his start() method so jus need manage onRequestPermissionsResult
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

    private boolean isSavingPicture() {
        return mPictureSaverTask != null && mPictureSaverTask.getStatus() == AsyncTask.Status.RUNNING;
    }

    public void capturePicture() {
        if (mCapturingPicture || isSavingPicture()) {
            return;
        }
        mCapturingPicture = true;
        mCameraView.capturePicture();
    }

    private void savePicture(byte[] jpeg) {
        mCapturingPicture = false;
        mPictureSaverTask = new PictureSaverTask();
        mPictureSaverTask.execute(new ImageBox(jpeg));
    }

    private void cleanPictureDir() {
        Timber.d("Clean picture dir request");
        if (mPictureDirCleanerTask != null) {
            mPictureDirCleanerTask.stop();
        }
        mPictureDirCleanerTask = new PictureDirCleanerTask();
        mPictureDirCleanerTask.execute();
    }

    /**
     * NOT USED!
     * CameraView alredy managed in from CameraView and onRequestPermissionsResult
     * Use Dexter library to manage Permissions
     *
     * @see <a href="https://github.com/Karumi/Dexter</a>
     */
    private void permissionsMgr() {
        DialogOnDeniedPermissionListener dialogListener = DialogOnDeniedPermissionListener.Builder
                .withContext(getContext())
                .withTitle("Camera permission")
                .withMessage("Camera permission is needed to take pictures of your cat")
                .withButtonText(android.R.string.ok)
                .withIcon(R.drawable.ic_announcement)
                .build();

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(dialogListener)
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Timber.e("Permissions error: %s", error.toString());
                    }
                })
                .check();
    }

}
