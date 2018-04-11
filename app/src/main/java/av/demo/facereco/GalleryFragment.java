package av.demo.facereco;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import av.demo.facereco.picasso.FaceCenterCrop;

/**
 * Created by Antonio Vitiello on 10/04/2018.
 */

public class GalleryFragment extends Fragment {
    private static final FaceCenterCrop sFaceCenterCrop = new FaceCenterCrop(
            MyApplication.getIntResource(R.integer.image_target_width),
            MyApplication.getIntResource(R.integer.image_target_height));
    private static final String ARG_PICTURE_FILE = "arg_picture_file";
    private ImageView mPictureImageView;
    private File mPicture;
    private Picasso mPicasso;

    public GalleryFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GalleryFragment newInstance(File pictureFile) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PICTURE_FILE, pictureFile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPicasso = Picasso.get();
        mPicasso.setIndicatorsEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        initComponent(rootView);
        return rootView;
    }

    private void initComponent(View rootView) {
        mPicture = (File)getArguments().getSerializable(ARG_PICTURE_FILE);
        mPictureImageView = rootView.findViewById(R.id.picture_iv);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isFaceCenterActivated = MyApplication.getBooleanResource(R.bool.face_center_activated);
        if(isFaceCenterActivated){
            loadPictureFaceCenter();
        } else {
            loadPicture();
        }
    }

    private void loadPicture(){
        //Width, Height in pixel
        int targetWidth = MyApplication.getIntResource(R.integer.image_target_width);
        int targetHeight = MyApplication.getIntResource(R.integer.image_target_height);
        mPicasso.load(mPicture)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .into(mPictureImageView);
    }

    private void loadPictureFaceCenter(){
        //Width, Height in pixel
        int targetWidth = MyApplication.getIntResource(R.integer.image_target_width);
        int targetHeight = MyApplication.getIntResource(R.integer.image_target_height);
        mPicasso.load(mPicture)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .transform(sFaceCenterCrop)
                .into(mPictureImageView);
    }

}
