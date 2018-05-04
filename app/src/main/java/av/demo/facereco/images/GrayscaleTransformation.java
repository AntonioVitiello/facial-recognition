package av.demo.facereco.images;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;


/**
 * Created by Antonio Vitiello on 04/05/2018.
 */

public class GrayscaleTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setColorFilter(filter);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(source, 0, 0, paint);

        source.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "grayscaleTransformation()";
    }

}