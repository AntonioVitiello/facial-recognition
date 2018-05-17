package av.demo.facereco.images;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Antonio Vitiello on 17/04/2018.
 */

public class ImageBox {
    private Bitmap bitmap;
    private File file;

    public ImageBox(Bitmap bitmap, File file){
        this.bitmap = bitmap;
        this.file = file;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public File getFile() {
        return file;
    }

}
