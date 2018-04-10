package av.demo.facereco;

import android.app.Application;
import android.support.annotation.NonNull;

import av.demo.facereco.logger.TimberLogImplementation;

/**
 * Created by Antonio Vitiello on 06/04/2018.
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // Timber initialization
        TimberLogImplementation.init(getStringResource(R.string.app_name));
    }

    @NonNull
    public static String getStringResource(int resId) {
        return sInstance.getString(resId);
    }

    @NonNull
    public static String getStringResource(int resId, Object... formatArgs) {
        return sInstance.getString(resId, formatArgs);
    }

    public static int getIntResource(int resId) {
        return sInstance.getResources().getInteger(resId);
    }

}
