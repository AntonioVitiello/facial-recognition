package av.demo.facereco.adapters;

/**
 * Created by Antonio Vitiello on 10/04/2018.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.File;

import av.demo.facereco.GalleryFragment;
import av.demo.facereco.files.Utils;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class GalleryPagerAdapter extends FragmentPagerAdapter {
    private File[] mPictureFiles;
    private Context mContext;

    public GalleryPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mPictureFiles = Utils.listPictureSortByModified(Utils.SORT_ORDER_DESCENDING);
    }

    @Override
    public Fragment getItem(int position) {
        return GalleryFragment.newInstance(mPictureFiles[position]);
    }

    @Override
    public int getCount() {
        return mPictureFiles.length;
    }
}
