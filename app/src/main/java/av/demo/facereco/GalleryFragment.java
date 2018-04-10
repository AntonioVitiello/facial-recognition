package av.demo.facereco;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rohitarya.picasso.facedetection.transformation.FaceCenterCrop;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Antonio Vitiello on 10/04/2018.
 */

public class GalleryFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_PICTURE_FILE = "arg_picture_file";
    private ImageView mPictureImageView;
    private File mPicture;

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
        Picasso.with(getContext())
                .load(mPicture)
                .fit() // use fit() and centerInside() for making it memory efficient.
                .centerInside()
                .transform(new FaceCenterCrop(100, 100)) //in pixels. You can also use FaceCenterCrop(width, height, unit) to provide width, height in DP.
                .into(mPictureImageView);
    }
}
