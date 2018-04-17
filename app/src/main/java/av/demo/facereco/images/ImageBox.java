package av.demo.facereco.images;

/**
 * Created by Antonio Vitiello on 17/04/2018.
 */

public class ImageBox {
    private final byte[] imageBytes;

    public ImageBox(byte[] imageBytes){
        this.imageBytes = imageBytes;
    }

    public byte[] getBytes() {
        return imageBytes;
    }
}
