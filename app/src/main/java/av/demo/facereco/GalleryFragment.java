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

import av.demo.facereco.event.FaceCenterEvent;
import av.demo.facereco.facedetect.FaceCenterCrop;
import timber.log.Timber;

/**
 * Created by Antonio Vitiello on 10/04/2018.
 */

public class GalleryFragment extends Fragment {
    private static final String PICTURE_FILE_KEY = "picture_file_key";
    private ImageView mPictureImageView;
    private File[] mPictures;
    private Picasso mPicasso;
    private boolean mIsFaceCenterActivated;

    // TODO: 12/04/2018 Just for test
    private static final FaceCenterCrop sFaceCenterCrop = new FaceCenterCrop(
            MyApplication.getIntResource(R.integer.image_target_width),
            MyApplication.getIntResource(R.integer.image_target_height));


    public GalleryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GalleryFragment newInstance(File[] pictureFiles) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable(PICTURE_FILE_KEY, pictureFiles);
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
        mPictures = (File[]) getArguments().getSerializable(PICTURE_FILE_KEY);
        mPictureImageView = rootView.findViewById(R.id.picture_iv);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPicture();
    }

    private void loadPicture() {
        if (mIsFaceCenterActivated) {
            loadPictureFaceCenter();
        } else {
            loadPictureNatural();
        }
    }

    private void loadPictureNatural() {
        int targetMaxSize = MyApplication.getIntResource(R.integer.image_target_max_size);
        mPicasso.load(mPictures[0])
                .resize(targetMaxSize, targetMaxSize)
                .centerInside()
                .into(mPictureImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (mPictures.length > 1) {
                            cacheNextPicture();
                        }
                    }

                    @Override
                    public void onError(Exception exc) {
                        Timber.e(exc, "Error while loading picture file: %s", mPictures[0]);
                    }
                });
    }

    // TODO: 12/04/2018 Just for test Vision face detection
    private void loadPictureFaceCenter() {
        int targetMaxSize = MyApplication.getIntResource(R.integer.image_target_max_size);
        mPicasso.load(mPictures[0])
                .resize(targetMaxSize, targetMaxSize)
                .centerInside()
                .transform(sFaceCenterCrop)
                .into(mPictureImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (mPictures.length > 1) {
                            cacheNextPicture();
                        }
                    }

                    @Override
                    public void onError(Exception exc) {
                        Timber.e(exc, "Error while loading picture file: %s", mPictures[0]);
                    }
                });
    }

    private void cacheNextPicture() {
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

    @Override
    public void onStart() {
        super.onStart();
        // Register EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister EventBus
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReviewsEvent(FaceCenterEvent event) {
        mIsFaceCenterActivated = !mIsFaceCenterActivated;
        loadPicture();
    }

}
