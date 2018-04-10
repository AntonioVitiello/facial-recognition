package av.demo.facereco.adapters;

/**
 * Created by Antonio Vitiello on 10/04/2018.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import av.demo.facereco.GalleryFragment;
import timber.log.Timber;

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
        getPictureFiles();
    }

    private void getPictureFiles(){
        try {
            File externalCacheDir = mContext.getExternalCacheDir();
            mPictureFiles = externalCacheDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return !file.isDirectory();
                }
            });
        } catch (Exception exc) {
            mPictureFiles = new File[0];
            Timber.e(exc, "Error while reading picture dir: %s", Arrays.asList(mPictureFiles));
        }
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
