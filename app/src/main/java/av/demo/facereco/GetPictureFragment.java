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

import java.io.File;

import av.demo.facereco.files.ImageSaver;
import av.demo.facereco.scheduler.FileCleanerScheduler;
import av.demo.facereco.scheduler.PictureScheduler;
import timber.log.Timber;

/**
 * Created by Vitiello Antonio on 07/04/2018.
 */

public class GetPictureFragment extends Fragment implements PictureScheduler.Subscriber {
    private CameraView mCameraView;
    private ImageSaver mImageSaver;

    public GetPictureFragment() {
    }

    public static GetPictureFragment newInstance() {
        return new GetPictureFragment();
    }

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        initDaemons();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_picturing, container, false);
        initComponents(rootView);
        return rootView;
    }

    private void initDaemons() {
        // Start picture files cleaner daemon
        mImageSaver = new ImageSaver(getContext());
        File outputDir = mImageSaver.getOutputDir();
        FileCleanerScheduler.getInstance().start(outputDir);
    }

    private void initComponents(View rootView) {
        mCameraView = rootView.findViewById(R.id.camera);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mCameraView.start();
            PictureScheduler.getInstance().start(this);
            FileCleanerScheduler.getInstance().start();
        } catch (final Exception exc) {
            PictureScheduler.getInstance().stop(this);
            Timber.e(exc, "Error while starting capture image.");
        }
    }

    @Override
    public void onPause() {
        try {
            mCameraView.stop();
            FileCleanerScheduler.getInstance().stop();
        } catch (final Exception exc) {
            Timber.e(exc, "Error while stopping capture image.");
        } finally {
            PictureScheduler.getInstance().stop(this);
            super.onPause();
        }
    }

    //@OnCameraKitEvent(CameraKitImage.class)
    public void imageCaptured(CameraKitImage image) {
        try {
            byte[] jpeg = image.getJpeg();
            mImageSaver.setImage(jpeg);
            new Thread(mImageSaver).run();
        } catch (Exception exc) {
            Timber.e(exc, "Error while preparing image.");
        }
    }

    @Override
    public void onSchedule() {
        captureImage();
    }

    public void captureImage() {
        try {
            mCameraView.captureImage(new CameraKitEventCallback<CameraKitImage>() {
                @Override
                public void callback(CameraKitImage event) {
                    imageCaptured(event);
                }
            });
        } catch (Exception exc) {
            Timber.e(exc, "Error while capturing image.");
        }
    }

}
