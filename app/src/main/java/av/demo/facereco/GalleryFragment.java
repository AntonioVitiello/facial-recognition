package av.demo.facereco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Arrays;

import av.demo.facereco.event.MenuTapEvent;
import av.demo.facereco.worker.DetectWorkerThread;
import timber.log.Timber;


/**
 * Created by Antonio Vitiello on 10/04/2018.
 */

public class GalleryFragment extends Fragment {
    private static final String PICTURE_FILES_KEY = "picture_files_key";
    private ImageView mPictureImageView;
    private File[] mPictures;
    private Picasso mPicasso;
    private boolean isLandmarkDraw;

    public GalleryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GalleryFragment newInstance(File[] pictures, int startIndex) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        File[] files = Arrays.copyOfRange(pictures, startIndex, Math.min(pictures.length, startIndex + 1));
        args.putSerializable(PICTURE_FILES_KEY, files);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPicasso = Picasso.get();
        // Add triangle on image left corner: red for net loaded, blue for disk loaded, green for memory loaded
        mPicasso.setIndicatorsEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mPictures = (File[]) getArguments().getSerializable(PICTURE_FILES_KEY);
        mPictureImageView = rootView.findViewById(R.id.picture_iv);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
            loadPicture();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadPicture() {
        int targetMaxSize = MyApplication.getIntResource(R.integer.image_target_max_size);
        mPicasso.load(mPictures[0])
                .resize(targetMaxSize, targetMaxSize)
                .centerInside()
                .into(mPictureImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        cacheNextPicture();
                    }

                    @Override
                    public void onError(Exception exc) {
                        Timber.e(exc, "Error while loading picture file: %s", mPictures[0]);
                    }
                });
    }

    private void cacheNextPicture() {
        if (mPictures.length < 2) { //Nothing to cache
            return;
        }

        int targetMaxSize = MyApplication.getIntResource(R.integer.image_target_max_size);
        mPicasso.load(mPictures[1])
                .resize(targetMaxSize, targetMaxSize)
                .centerInside()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    //Eventbus event
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenuTapEvent(MenuTapEvent event) {
        if (!getUserVisibleHint()) {
            return;
        }
        switch (event.getmItemId()) {
            case MenuTapEvent.DETECT_FACE: {
                if (isLandmarkDraw) {
                    loadPicture();
                } else {
                    faceDetect();
                }
                isLandmarkDraw = !isLandmarkDraw;
                break;
            }
            case MenuTapEvent.RECOGNIZE_DIR: {
                // TODO: 17/05/2018 TODO
                Timber.e("RECOGNIZE_DIR: TODO!");
                break;
            }
            default:
                Timber.e("Invalid menu item id: %s", event.getmItemId());
        }
    }

    /**
     * Start Face Detection
     */
    private void faceDetect() {
        DetectWorkerThread.detectEnque(mPictures[0], mPictureImageView, getContext());
    }

}